package foregg.foreggserver.service.dailyService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.DailyConverter;
import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.EmotionRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.DailyRepository;
import foregg.foreggserver.repository.SideEffectRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.recordService.RecordQueryService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Security;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DailyService {

    private final DailyRepository dailyRepository;
    private final UserQueryService userQueryService;
    private final RecordQueryService recordQueryService;
    private final SideEffectRepository sideEffectRepository;
    private final FcmService fcmService;

    public void putEmotion(Long id, EmotionRequestDTO dto) {
        Daily daily = dailyRepository.findById(id).orElseThrow(() -> new RecordHandler(NOT_FOUND_DAILY));
        daily.setEmotionType(dto.getEmotionType());
    }

    public void writeDaily(DailyRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Optional<Daily> daily = dailyRepository.findByUserAndDate(user,DateUtil.formatLocalDateTime(LocalDate.now()));
        if (daily.isPresent()) {
            throw new RecordHandler(ALREADY_WRITTEN);
        }
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "새로운 하루기록이 등록되었습니다", String.format("%s님이 기록을 추가했습니다.", user.getNickname()), "today record male", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        dailyRepository.save(DailyConverter.toDaily(dto, user));
    }

    public void writeSideEffect(SideEffectRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Record hospitalRecord = recordQueryService.getNearestHospitalRecord();
        SideEffect sideEffect = DailyConverter.toSideEffect(dto, hospitalRecord, user);
        sideEffectRepository.save(sideEffect);
    }

    public List<SideEffectResponseDTO> getSideEffectList() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Optional<List<SideEffect>> foundSideEffect = sideEffectRepository.findByUser(user);
        if (foundSideEffect.isEmpty()) {
            return null;
        }
        List<SideEffect> sideEffects = foundSideEffect.get();
        return DailyConverter.toSideEffectResponseDTO(sideEffects);
    }

    public void shareInjection() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "주사 푸시 알림입니다", String.format("%s님이 주사를 맞았습니다.", user.getNickname()),"injection male", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else{
            throw new UserHandler(SPOUSE_NOT_FOUND);
        }
    }
}
