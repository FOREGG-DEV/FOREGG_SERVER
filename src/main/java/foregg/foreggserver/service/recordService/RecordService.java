package foregg.foreggserver.service.recordService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.RecordConverter;
import foregg.foreggserver.converter.RepeatTimeConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.recordDTO.MedicalRecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.MedicalRecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.RecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.repository.SideEffectRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.notificationService.NotificationService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecordService {

    private final RecordRepository recordRepository;
    private final RecordQueryService recordQueryService;
    private final RepeatTimeRepository repeatTimeRepository;
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final FcmService fcmService;
    private final NotificationService notificationService;
    private final DailyQueryService dailyQueryService;
    private final SideEffectRepository sideEffectRepository;

    private final Map<Long, List<ScheduledFuture<?>>> scheduledTasks = new ConcurrentHashMap<>();

    //일정 추가하기
    public RecordResponseDTO addRecord(RecordRequestDTO dto) {
        User user = getUser(SecurityUtil.getCurrentUser());
        Record nearestHospitalRecord = recordQueryService.getNearestHospitalRecord(LocalDate.now());
        List<SideEffect> sideEffects = sideEffectRepository.findByUserAndRecord(user, nearestHospitalRecord);
        List<SideEffect> nullSideEffect = dailyQueryService.getNullAndAfterTodaySideEffect();

        Record record = recordRepository.save(RecordConverter.toRecord(dto,user));
        Record newNearestHospitalRecord = recordQueryService.getNearestHospitalRecord(LocalDate.now());

        List<RepeatTime> repeatTimes = dto.getRepeatTimes();
        for (RepeatTime time : repeatTimes) {
            RepeatTime repeatTime = RepeatTimeConverter.toRepeatTime(time, record);
            repeatTimeRepository.save(repeatTime);
        }

        if (dto.getRecordType() == RecordType.HOSPITAL) {
            for (SideEffect sf : nullSideEffect) {
                sf.setRecord(newNearestHospitalRecord);
            }
            if (nearestHospitalRecord != null) {
                for (SideEffect sf : sideEffects) {
                    sf.setRecord(newNearestHospitalRecord);
                }
            }
        }

        if (dto.getRecordType() == RecordType.INJECTION) {
            notificationService.scheduleNotifications(user, record, repeatTimes);
        }
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "새로운 일정이 등록되었습니다", String.format("%s님이 일정을 추가했어요", user.getNickname()),"calendar",null,null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return getRepeatTimes(record);
    }

    //일정 삭제하기
    public void deleteRecord(Long id) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Record record = recordRepository.findByIdAndUser(id,user).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        notificationService.cancelScheduledTasks(id);
        if (record.getType() == RecordType.HOSPITAL) {
            List<SideEffect> sideEffect = record.getSideEffect();
            for (SideEffect sf : sideEffect) {
                sf.setRecord(null);
            }
        }
        recordRepository.delete(record);

        //부작용 일정 붙이기
        List<SideEffect> orphanSideEffect = getOrphanSideEffect();
        for (SideEffect sf : orphanSideEffect) {
            Record nearestHospitalRecord =
                    recordQueryService.getNearestHospitalRecord(DateUtil.toLocalDate(sf.getDate()));
            sf.setRecord(nearestHospitalRecord);
        }
    }

    //일정 변경하기
    public void modifyRecord(Long id, RecordRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Record record = recordRepository.findByIdAndUser(id,user).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        notificationService.cancelScheduledTasks(id);
        record.updateRecord(dto);

        List<SideEffect> sideEffects = record.getSideEffect();
        for (SideEffect sf : sideEffects) {
            sf.setRecord(recordQueryService.getNearestHospitalRecord(DateUtil.toLocalDate(sf.getDate())));
        }

        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if(repeatTimes.isPresent()){
            List<RepeatTime> foundRepeatTimes = repeatTimes.get();
            for (RepeatTime time : foundRepeatTimes) {
                repeatTimeRepository.delete(time);
            }
        }
        List<RepeatTime> updateTimes = dto.getRepeatTimes();
        for (RepeatTime time : updateTimes) {
            RepeatTime saveTime = RepeatTimeConverter.toRepeatTime(time, record);
            repeatTimeRepository.save(saveTime);
        }

        if (dto.getRecordType() == RecordType.INJECTION) {
            notificationService.scheduleNotifications(getUser(SecurityUtil.getCurrentUser()), record, updateTimes);
        }
    }

    //일정 상세 보기
    public RecordResponseDTO recordDetail(Long id) {
        User user;
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            user = userQueryService.returnSpouse();
        }else{
            user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        }
        Record record = recordRepository.findByIdAndUser(id, user).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        return getRepeatTimes(record);
    }

    //진료기록 추가하기
    public void addMedicalRecord(Long id, MedicalRecordRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Record record = recordRepository.findByIdAndUser(id,user).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        if (record.getType() != RecordType.HOSPITAL) {
            throw new RecordHandler(NOT_HOSPITAL_RECORD);
        }
        record.setMedical_record(dto.getMedicalRecord());
        recordRepository.save(record);
    }

    //진료기록 및 부작용 확인하기
    public MedicalRecordResponseDTO medicalRecordAndSideEffect(Long id) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Record record = recordRepository.findByIdAndUser(id,user).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        if (record.getType() != RecordType.HOSPITAL) {
            throw new RecordHandler(NOT_HOSPITAL_RECORD);
        }
        return RecordConverter.toMedicalRecordResponse(record);
    }

    //RepeatTime 추출하기
    private RecordResponseDTO getRepeatTimes(Record record) {
        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if(repeatTimes.isPresent()){
            List<RepeatTime> result = repeatTimes.get();
            return RecordConverter.toRecordResponseDTO(record, result);
        }
        return RecordConverter.toRecordResponseDTO(record, null);
    }

    private User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
    }

    private List<SideEffect> getOrphanSideEffect() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        return sideEffectRepository.

                findByUserAndRecord(user, null);
    }

    private boolean newIsEarlier(String newDate, String oldDate) {
        if (DateUtil.toLocalDate(newDate).isBefore(DateUtil.toLocalDate(oldDate))) {
            return true;
        }
        return false;
    }

}
