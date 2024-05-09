package foregg.foreggserver.service.dailyService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.converter.DailyConverter;
import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.EmotionRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.DailyRepository;
import foregg.foreggserver.repository.SideEffectRepository;
import foregg.foreggserver.service.recordService.RecordQueryService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.time.LocalDate;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.ALREADY_WRITTEN;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_DAILY;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DailyService {

    private final DailyRepository dailyRepository;
    private final UserQueryService userQueryService;
    private final RecordQueryService recordQueryService;
    private final SideEffectRepository sideEffectRepository;

    public void putEmotion(Long id, EmotionRequestDTO dto) {
        Daily daily = dailyRepository.findById(id).orElseThrow(() -> new RecordHandler(NOT_FOUND_DAILY));
        log.info("데일리 " + daily.getContent());
        log.info("이모지리퀘 " + dto.getEmotionType());

        daily.setEmotionType(dto.getEmotionType());
        log.info("데일리 이모지 " + daily.getEmotionType());

    }

    public void writeDaily(DailyRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Optional<Daily> daily = dailyRepository.findByDate(DateUtil.formatLocalDateTime(LocalDate.now()));
        if (daily.isPresent()) {
            throw new RecordHandler(ALREADY_WRITTEN);
        }
        dailyRepository.save(DailyConverter.toDaily(dto, user));
    }

    public void writeSideEffect(SideEffectRequestDTO dto) {
        Record hospitalRecord = recordQueryService.getNearestHospitalRecord();
        SideEffect sideEffect = DailyConverter.toSideEffect(dto, hospitalRecord);
        sideEffectRepository.save(sideEffect);
    }
}
