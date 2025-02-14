package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.enums.ChallengeEmojiType;
import foregg.foreggserver.domain.enums.NavigationType;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO.MyChallengeDTO;
import foregg.foreggserver.repository.*;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.notificationService.NotificationService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;
import static foregg.foreggserver.dto.challengeDTO.ChallengeRequestDTO.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private static final String s3Url = "https://foregg-bucket.s3.ap-northeast-2.amazonaws.com/challenge/";

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    private final ChallengeQueryService challengeQueryService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
    private final ChallengeSuccessRepository challengeSuccessRepository;

    public void participate(Long id) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_OPEN));
        if (cp.isParticipating()) {
            throw new ChallengeHandler(ALREADY_PARTICIPATING);
        }
        cp.setParticipating(true);
        cp.setStartDate(LocalDate.now().toString());
    }

    public void quitChallenge(Long challengeId) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(challengeId).
                orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipations = challengeParticipationRepository.findByUserAndChallenge(user,challenge).
                orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        challengeParticipationRepository.delete(challengeParticipations);
    }

    public void deleteTodaySuccess(Long id) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipation = challengeParticipationRepository.findByUserAndChallenge(user, challenge).
                orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        Optional<ChallengeSuccess> foundChallengeSuccess = challengeSuccessRepository.findByChallengeParticipationAndDate(challengeParticipation, LocalDate.now().toString());
        if (foundChallengeSuccess.isEmpty()) {
            throw new ChallengeHandler(NO_SUCCESS_DAY);
        }
        challengeSuccessRepository.delete(foundChallengeSuccess.get());
    }

    public String createChallengeName(ChallengeNameRequestDTO dto) {
        User user = userQueryService.getUser();
        if (user.getChallengeName() != null) {
            return "301";
        }

        if (user.getChallengeName() != null) {
            throw new ChallengeHandler(NICKNAME_EXIST);
        }
        User byChallengeName = userRepository.findByChallengeName(dto.getChallengeNickname());
        if (byChallengeName != null) {
            throw new ChallengeHandler(NICKNAME_DUPLICATE);
        }
        user.setChallengeName(dto.getChallengeNickname());
        user.addPoint(2000);
        return "200";
    }

    public void unlock(Long id) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        Optional<ChallengeParticipation> foundCp = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
        if (foundCp.isPresent()) {
            throw new ChallengeHandler(ALREADY_OPEN);
        }
        user.deductPoint(700);
        ChallengeParticipation cp = ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .isOpen(true)
                .isParticipating(false)
                .build();
        challengeParticipationRepository.save(cp);
    }

    public void createChallenge(ChallengeCreateRequestDTO dto) {
        User user = userQueryService.getUser();
        user.deductPoint(1000);
        Challenge challenge = Challenge.builder().name(dto.getName()).description(dto.getDescription())
                .image(s3Url+convertToS3Url(dto.getChallengeEmojiType()))
                .producerId(user.getId())
                .build();
        challengeRepository.save(challenge);

        ChallengeParticipation cp = ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .isOpen(true)
                .isParticipating(true)
                .startDate(LocalDate.now().toString())
                .build();
        challengeParticipationRepository.save(cp);
    }

    public void success(Long challengeId, String date, ChallengeCompleteRequestDTO dto) {
        User user = userQueryService.getUser();
        String thoughts = null;
        if (dto != null) {
            thoughts = dto.getThoughts();
        }

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cParticipation = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        if (!cParticipation.isParticipating()) {
            throw new ChallengeHandler(NO_PARTICIPATING_CHALLENGE);
        }

        //오늘 날짜일때
        if(date.equals(LocalDate.now().toString())){
            if (challengeSuccessRepository.findByChallengeParticipationAndDate(cParticipation,LocalDate.now().toString()).isPresent()) {
                throw new ChallengeHandler(DUPLICATED_SUCCESS_DATE);
            }
            ChallengeSuccess challengeSuccess = ChallengeSuccess.builder().date(LocalDate.now().toString())
                    .challengeParticipation(cParticipation).comment(thoughts).build();
            challengeSuccessRepository.save(challengeSuccess);
            user.addPoint(100);
        } //어제 날짜일때
        else if (date.equals(LocalDate.now().minusDays(1).toString())) {
            if (challengeSuccessRepository.findByChallengeParticipationAndDate(cParticipation,LocalDate.now().minusDays(1).toString()).isPresent()) {
                throw new ChallengeHandler(DUPLICATED_SUCCESS_DATE);
            }
            ChallengeSuccess challengeSuccess = ChallengeSuccess.builder().date(LocalDate.now().minusDays(1).toString())
                    .challengeParticipation(cParticipation).comment(thoughts).build();
            challengeSuccessRepository.save(challengeSuccess);
            user.addPoint(50);
        }else{
            throw new ChallengeHandler(OUT_OF_VALIDATE_DAYS);
        }
    }

    public void cheer(Long receiverId, NotificationType type, Long challengeId) {
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        User sender = userQueryService.getUser();
        Challenge challenge = challengeQueryService.isParticipating(challengeId);

        ChallengeParticipation challengeParticipation = challengeParticipationRepository.findByUserAndChallenge(receiver, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        if (!challengeParticipation.isParticipating()) {
            throw new ChallengeHandler(NO_PARTICIPATING_CHALLENGE);
        }

        catchCheerException(challengeParticipation, type);

        List<Notification> notificationList = notificationRepository.findBySenderAndDateAndNotificationType(sender.getChallengeName(), LocalDate.now().toString(), type);
        if (notificationList.size() >= 3) {
            throw new ChallengeHandler(NO_MORE_THAN_THIRD_TIME);
        }

        if (notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(sender.getChallengeName(), receiver, LocalDate.now().toString(), type) != null) {
            throw new ChallengeHandler(ALREADY_SEND_CHEER);
        }

        Notification notification = notificationService.createNotification(type, receiver, sender.getChallengeName(), challengeId);
        notificationRepository.save(notification);
        cheerAlarm(receiver, sender, type, challengeId);
    }

    private void cheerAlarm(User receiver, User sender, NotificationType type, Long challengeId) {
        String alarmType;
        String rear;
        if (type.equals(NotificationType.SUPPORT)) {
            alarmType = "응원";
            rear = "이";
        }else{
            alarmType = "박수";
            rear = "가";
        }
        try {
            fcmService.sendMessageTo(receiver.getFcmToken(), String.format("다른 사용자로부터 %s받았을 때",alarmType), String.format("%s님으로부터 %s%s 도착했어요. 오늘의 챌린지를 달성하러 가볼까요?",sender.getChallengeName(),alarmType,rear), NavigationType.challenge_support.toString()+"/"+challengeId, null, null, null);
            log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", receiver.getNickname());
        } catch (IOException e) {
            log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void catchCheerException(ChallengeParticipation challengeParticipation, NotificationType notificationType) {
        if (challengeSuccessRepository.findByChallengeParticipationAndDate(challengeParticipation, LocalDate.now().toString()).isPresent() && notificationType.equals(NotificationType.SUPPORT)) {
            throw new ChallengeHandler(UNABLE_TO_SEND_SUPPORT);
        }

        if (challengeSuccessRepository.findByChallengeParticipationAndDate(challengeParticipation, LocalDate.now().toString()).isEmpty() && notificationType.equals(NotificationType.CLAP)) {
            throw new ChallengeHandler(UNABLE_TO_SEND_CLAP);
        }
    }

    private String convertToS3Url(ChallengeEmojiType type) {
        String extend = ".png";
        return type.toString() + extend;
    }
}
