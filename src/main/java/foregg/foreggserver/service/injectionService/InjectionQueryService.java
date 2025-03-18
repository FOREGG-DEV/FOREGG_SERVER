package foregg.foreggserver.service.injectionService;

import foregg.foreggserver.apiPayload.exception.handler.DailyHandler;
import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.NavigationType;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.injectionDTO.MedicalResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.MedicalRepository;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InjectionQueryService {

    private final UserQueryService userQueryService;
    private final FcmService fcmService;
    private final MedicalRepository medicalRepository;
    private final RecordRepository recordRepository;
    private final RepeatTimeRepository repeatTimeRepository;

    public void shareMedical(Long id, String date, String time) {
        User user = userQueryService.getUser();
        Optional<Record> foundRecord = recordRepository.findByIdAndUser(id, user);
        if (foundRecord.isEmpty()) {
            throw new RecordHandler(NOT_FOUND_MY_INJECTION_RECORD);
        }
        Record record = foundRecord.get();
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "투여 완료 공유 알림입니다", String.format("%s님이 일정을 완료했어요.", user.getNickname(),foundRecord.get().getName()),NavigationType.inj_med_info_screen.toString()+"/"+record.getType()+"/"+id+"/"+date+"/"+time, id.toString(), time, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else{
            throw new UserHandler(SPOUSE_NOT_FOUND);
        }
    }

    public MedicalResponseDTO getMedicalInfo(Long id, RecordType type, String date, String time) {

        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        isMyInjectionRecord(record);
        isValidateRecord(record, type, time, date);

        Optional<Medical> medical = medicalRepository.findByName(record.getName());
        if (medical.isPresent()) {
            return MedicalResponseDTO.builder()
                    .name(medical.get().getName())
                    .date(date)
                    .description(medical.get().getDescription())
                    .image(medical.get().getImage())
                    .time(time).build();
        }

        return MedicalResponseDTO.builder()
                .name(record.getName())
                .date(date)
                .description(null)
                .image(null)
                .time(time).build();
    }

    public void isMyInjectionRecord(Record record) {
        User recordUser = record.getUser();
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            if (!recordUser.equals(userQueryService.returnSpouse())) {
                throw new RecordHandler(NOT_FOUND_MY_INJECTION_RECORD);
            }
        }else{
            User user = userQueryService.getUser();
            if (!recordUser.equals(user)) {
                throw new RecordHandler(NOT_FOUND_MY_INJECTION_RECORD);
            }
        }
    }

    private void isValidateRecord(Record record, RecordType type, String time, String date) {
        if (type.equals(RecordType.ETC) || type.equals(RecordType.HOSPITAL)) {
            throw new DailyHandler(ONLY_INJECTION_MEDICINE);
        }

        if (!record.getType().equals(type)) {
            throw new DailyHandler(MISMATCH_RECORD_AND_TYPE);
        }

        Optional<RepeatTime> repeatTime = repeatTimeRepository.findByRecordAndTime(record, time);
        if (repeatTime.isEmpty()) {
            throw new RecordHandler(NOT_FOUND_REPEATTIME);
        }

        if (record.getDate() != null) {
            if (!record.getDate().equals(date)) {
                throw new RecordHandler(INVALID_RECORD_DATE);
            }
        } else {
            List<String> intervalDates = DateUtil.getIntervalDates(record.getStart_date(), record.getEnd_date());
            if (!intervalDates.contains(date)) {
                throw new RecordHandler(INVALID_RECORD_DATE);
            }
            if (!record.getRepeat_date().equals("매일") && !record.getRepeat_date().contains(DateUtil.getKoreanDayOfWeek(date))) {
                throw new RecordHandler(INVALID_RECORD_DATE);
            }
        }
    }
}
