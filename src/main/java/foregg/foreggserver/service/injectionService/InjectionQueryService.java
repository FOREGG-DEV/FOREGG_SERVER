package foregg.foreggserver.service.injectionService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.injectionDTO.InjectionResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.InjectionRepository;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InjectionQueryService {

    private final UserQueryService userQueryService;
    private final FcmService fcmService;
    private final InjectionRepository injectionRepository;
    private final RecordRepository recordRepository;
    private final RepeatTimeRepository repeatTimeRepository;

    public void shareInjection(Long id, String time) {
        User user = userQueryService.getUser();
        Optional<Record> foundRecord = recordRepository.findByIdAndUser(id, user);
        if (foundRecord.isEmpty()) {
            throw new RecordHandler(NOT_FOUND_MY_INJECTION_RECORD);
        }

        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "주사 푸시 알림입니다", String.format("%s님이 일정을 완료했어요.", user.getNickname(),foundRecord.get().getName()),"inj_med_info_screen", id.toString(),time, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else{
            throw new UserHandler(SPOUSE_NOT_FOUND);
        }
    }

    public InjectionResponseDTO getInjectionInfo(Long id, String time) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        isMyInjectionRecord(record);
        if (record.getType() != RecordType.INJECTION) {
            throw new RecordHandler(NOT_INJECTION_RECORD);
        }

        Optional<RepeatTime> repeatTime = repeatTimeRepository.findByRecordAndTime(record, time);
        if (repeatTime.isEmpty()) {
            throw new RecordHandler(NOT_FOUND_REPEATTIME);
        }

        Optional<Injection> injection = injectionRepository.findByName(record.getName());
        if (injection.isPresent()) {
            return InjectionResponseDTO.builder()
                    .name(injection.get().getName())
                    .description(injection.get().getDescription())
                    .image(injection.get().getImage())
                    .time(time).build();
        }

        return InjectionResponseDTO.builder()
                .name(record.getName())
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
}
