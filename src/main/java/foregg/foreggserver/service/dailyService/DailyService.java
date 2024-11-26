package foregg.foreggserver.service.dailyService;

import foregg.foreggserver.apiPayload.exception.handler.DailyHandler;
import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.DailyConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO.DailyReplyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.DailyRepository;
import foregg.foreggserver.repository.ReplyRepository;
import foregg.foreggserver.repository.SideEffectRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.myPageService.MyPageQueryService;
import foregg.foreggserver.service.recordService.RecordQueryService;
import foregg.foreggserver.service.s3Service.S3Service;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final MyPageQueryService myPageQueryService;
    private final S3Service s3Service;
    private final ReplyRepository replyRepository;

    public void writeDaily(DailyRequestDTO dto, String imageUrl) {
        User user = userQueryService.getUser();
        Daily daily = dailyRepository.findByDateAndUser(DateUtil.formatLocalDateTime(LocalDate.now()),user);
        int count = myPageQueryService.getSurgeryCount();
        if (daily != null) {
            throw new RecordHandler(ALREADY_WRITTEN);
        }
        User spouse = userQueryService.returnSpouse();
        if (spouse != null) {
            try {
                fcmService.sendMessageTo(spouse.getFcmToken(), "새로운 하루기록이 등록되었습니다", String.format("%s님의 하루 기록이 도착했어요.", user.getNickname()), "today record male", null, null, null);
                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", spouse.getNickname());
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        dailyRepository.save(DailyConverter.toDaily(dto, user, imageUrl, count));
    }

    //회차 전체 삭제하기
    public void deleteByCount(int count) {
        User user = userQueryService.getUser();
        List<Daily> dailyList = dailyRepository.findByUserAndCount(user, count).orElseThrow(() -> new DailyHandler(NOT_FOUND_DAILY));
        for (Daily daily : dailyList) {
            s3Service.deleteFileByUrl(daily.getImage());
        }
        dailyRepository.deleteAll(dailyList);
    }

    public void reply(DailyReplyRequestDTO dto) {
        User wife = userQueryService.returnSpouse();
        Daily daily = dailyRepository.findByIdAndUser(dto.getId(), wife).orElseThrow(() -> new DailyHandler(NOT_FOUND_DAILY));
        if (daily.getReply() != null) {
            throw new DailyHandler(ALREADY_REPLY);
        }
        Reply reply = Reply.builder()
                .content(dto.getContent())
                .replyEmojiType(dto.getReplyEmojiType())
                .daily(daily)
                .receiver(wife)
                .sender(userQueryService.getUser())
                .build();
        replyRepository.save(reply);
        daily.setReply(reply);
    }

    public void writeSideEffect(SideEffectRequestDTO dto) {
        User user = userQueryService.getUser();
        Record hospitalRecord = recordQueryService.getNearestHospitalRecord(LocalDate.now());
        SideEffect sideEffect = DailyConverter.toSideEffect(dto, hospitalRecord, user);
        sideEffectRepository.save(sideEffect);
    }

    public List<SideEffectResponseDTO> getSideEffectList() {
        User user = userQueryService.getUser();
        Optional<List<SideEffect>> foundSideEffect = sideEffectRepository.findByUser(user);
        if (foundSideEffect.isEmpty()) {
            return null;
        }
        List<SideEffect> sideEffects = foundSideEffect.get();
        return DailyConverter.toSideEffectResponseDTO(sideEffects);
    }

    public void modifySideEffect(Long id, SideEffectRequestDTO dto) {
        User user = userQueryService.getUser();
        SideEffect sideEffect = sideEffectRepository.findByUserAndId(user, id).orElseThrow(() -> new RecordHandler(NOT_FOUND_SIDEEFFECT));
        sideEffect.setContent(dto.getContent());
    }

    public void deleteSideEffect(Long id) {
        User user = userQueryService.getUser();
        SideEffect sideEffect = sideEffectRepository.findByUserAndId(user, id).orElseThrow(() -> new RecordHandler(NOT_FOUND_SIDEEFFECT));
        sideEffectRepository.delete(sideEffect);
    }

    public void deleteDaily(Long id) {
        // 현재 사용자 가져오기
        User user = userQueryService.getUser();

        // 사용자와 ID에 해당하는 Daily 가져오기
        Daily daily = dailyRepository.findByIdAndUser(id, user).orElseThrow(() -> new RecordHandler(NOT_FOUND_DAILY));

        // S3에서 해당 이미지 파일 삭제 (URL인 경우 deleteFileByUrl 사용)
        if (daily.getImage() != null) {
            s3Service.deleteFileByUrl(daily.getImage());
        }

        // Daily 기록 삭제
        dailyRepository.delete(daily);
    }


    public void modifyDaily(Long id, DailyRequestDTO dto, MultipartFile image) throws IOException {
        User user = userQueryService.getUser();
        Daily daily = dailyRepository.findByIdAndUser(id, user).orElseThrow(() -> new RecordHandler(NOT_FOUND_DAILY));
        String date = daily.getDate();
        String imageUrl = null;
        if (image != null) {
            if (daily.getImage() != null) {
                s3Service.deleteFileByUrl(daily.getImage());
            }
            imageUrl = s3Service.upload(image);
        }
        daily.updateDaily(dto, imageUrl);
    }

}
