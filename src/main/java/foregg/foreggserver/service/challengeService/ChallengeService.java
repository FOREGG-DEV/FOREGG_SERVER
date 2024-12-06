package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO.MyChallengeDTO;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.repository.NotificationRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.notificationService.NotificationService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRepository;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;
    private final ChallengeQueryService challengeQueryService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public void participate(Long id) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_OPEN));
        if (cp.isParticipating()) {
            throw new ChallengeHandler(ALREADY_PARTICIPATING);
        }
        cp.setParticipating(true);
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
        List<String> successDays = challengeParticipation.getSuccessDays();
        if (successDays == null) {
            throw new ChallengeHandler(NO_SUCCESS_DAY);
        }
        successDays.remove(DateUtil.formatLocalDateTime(LocalDate.now()));
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
                .image(dto.getChallengeEmojiType())
                .producerId(user.getId())
                .build();
        challengeRepository.save(challenge);

        ChallengeParticipation cp = ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .isOpen(true)
                .isParticipating(true)
                .build();
        challengeParticipationRepository.save(cp);
    }

    public MyChallengeDTO success(Long challengeId, String todayDayOfWeek, ChallengeCompleteRequestDTO dto) {
        User user = userQueryService.getUser();

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cParticipation = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        if (!cParticipation.isParticipating()) {
            throw new ChallengeHandler(NO_PARTICIPATING_CHALLENGE);
        }
        if (cParticipation.getSuccessDays().contains(todayDayOfWeek)) {
            throw new ChallengeHandler(DUPLICATED_SUCCESS_DATE);
        }
        cParticipation.getSuccessDays().add(todayDayOfWeek);
        if (todayDayOfWeek.equals(DateUtil.getTodayDayOfWeek())) {
            user.addPoint(100);
            cParticipation.setThoughts(dto.getThoughts());
        } else if (todayDayOfWeek.equals(DateUtil.getYesterdayDayOfWeek())) {
            user.addPoint(50);
        } else {
            throw new ChallengeHandler(OUT_OF_VALIDATE_DAYS);
        }
        return ChallengeConverter.toMyChallengeDTO(cParticipation, challengeQueryService.getChallengeParticipants(cParticipation));
    }

    //챌린지 성공 날짜 초기화 메서드
    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void initSuccessDays() {
        List<ChallengeParticipation> challengeParticipations = challengeParticipationRepository.findAll();
        for (ChallengeParticipation cp : challengeParticipations) {
            cp.getSuccessDays().clear(); // successDays 초기화
        }
        challengeParticipationRepository.saveAll(challengeParticipations); // 변경 사항 저장
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

        List<Notification> notificationList = notificationRepository.findBySenderAndDateAndNotificationType(sender, LocalDate.now().toString(), type);
        if (notificationList.size() >= 3) {
            throw new ChallengeHandler(NO_MORE_THAN_THIRD_TIME);
        }

        if (notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(sender, receiver, LocalDate.now().toString(), type) != null) {
            throw new ChallengeHandler(ALREADY_SEND_CHEER);
        }

        Notification notification = notificationService.createNotification(type, receiver, sender, challengeId);
        notificationRepository.save(notification);
    }

    private void catchCheerException(ChallengeParticipation challengeParticipation, NotificationType notificationType) {
        if (challengeParticipation.getSuccessDays().contains(DateUtil.getTodayDayOfWeek()) && notificationType.equals(NotificationType.SUPPORT)) {
            throw new ChallengeHandler(UNABLE_TO_SEND_SUPPORT);
        }

        if (!challengeParticipation.getSuccessDays().contains(DateUtil.getTodayDayOfWeek()) && notificationType.equals(NotificationType.CLAP)) {
            throw new ChallengeHandler(UNABLE_TO_SEND_CLAP);
        }
    }
}
