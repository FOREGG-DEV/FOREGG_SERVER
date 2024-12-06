package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeParticipantsDTO;
import foregg.foreggserver.repository.NotificationRepository;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRespository;
    private final UserQueryService userQueryService;
    private final NotificationRepository notificationRepository;

    public ChallengeResponseDTO challengeMain() {
        User user = userQueryService.getUser();
        if (user.getChallengeName() == null) {
            throw new ChallengeHandler(MAKE_NICKNAME_FIRST);
        }
        List<ChallengeDTO> result = new ArrayList<>();

        //일단 관리자 id는 -1
        List<Challenge> challenges = getMainChallenge();
        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            result.add(challengeResponseDTO);
        }
        return ChallengeResponseDTO.builder().dtos(result).build();
    }

    public ChallengeResponseDTO getAllChallenges() {
        User user = userQueryService.getUser();
        List<ChallengeDTO> resultList = new ArrayList<>();
        List<Challenge> mainChallenge = challengeRepository.findByProducerId(-1L);
        for (Challenge challenge : mainChallenge) {
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            resultList.add(challengeResponseDTO);
        }

        resultList.addAll(getCustomChallenge());
        return ChallengeResponseDTO.builder().dtos(resultList).build();
    }

    public ChallengeResponseDTO.MyChallengeTotalDTO getMyChallenges() {
        List<ChallengeResponseDTO.MyChallengeTotalDTO.MyChallengeDTO> result = new ArrayList<>();
        List<ChallengeParticipation> cp = getMyCParticipation();
        for (ChallengeParticipation challengeParticipation : cp) {
            result.add(ChallengeConverter.toMyChallengeDTO(challengeParticipation, getChallengeParticipants(challengeParticipation)));
        }
        return ChallengeResponseDTO.MyChallengeTotalDTO.builder().dtos(result).firstDateOfWeek(DateUtil.getFirstDayOfWeek()).build();
    }

    public List<ChallengeParticipantsDTO> getParticipants(Long challengeId, boolean isSuccess) {

        List<ChallengeParticipantsDTO> result = new ArrayList<>();

        User currentUser = userQueryService.getUser();
        Challenge challenge = isParticipating(challengeId);
        List<ChallengeParticipation> challengeParticipations = challenge.getChallengeParticipations();

        challengeParticipations.removeIf(cp -> cp.getUser().equals(currentUser));

        if (isSuccess) {
            challengeParticipations.removeIf(cp -> !cp.getSuccessDays().contains(DateUtil.getTodayDayOfWeek()));
        }else{
            challengeParticipations.removeIf(cp -> cp.getSuccessDays().contains(DateUtil.getTodayDayOfWeek()));
        }

        if (challengeParticipations.isEmpty()) {
            return new ArrayList<>();
        }

        for (ChallengeParticipation cp : challengeParticipations) {
            Notification notification;
            if (isSuccess) {
                notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(currentUser, cp.getUser(), LocalDate.now().toString(), NotificationType.CLAP);
            } else {
                notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(currentUser, cp.getUser(), LocalDate.now().toString(), NotificationType.SUPPORT);
            }
            if (notification == null) {
                result.add(ChallengeConverter.toChallengeParticipantDTO(cp, false));
            } else {
                result.add(ChallengeConverter.toChallengeParticipantDTO(cp, true));
            }
        }
        return result;
    }

    //챌린지 검색 메서드
    public ChallengeResponseDTO searchChallenge(String keyword) {
        User user = userQueryService.getUser();
        List<Challenge> challenges = challengeRepository.findByNameContaining(keyword);
        List<ChallengeDTO> resultList = new ArrayList<>();
        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            resultList.add(challengeResponseDTO);
        }
        return ChallengeResponseDTO.builder().dtos(resultList).build();
    }

    public Challenge isParticipating(Long challengeId) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipation = challengeParticipationRespository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        if (!challengeParticipation.isParticipating()) {
            throw new ChallengeHandler((NO_PARTICIPATING_CHALLENGE));
        }
        return challenge;
    }

    private List<Challenge> getMainChallenge() {
        User user = userQueryService.getUser();
        List<Challenge> mainChallenge = challengeRepository.findByProducerId(-1L);
        List<Challenge> result = new ArrayList<>();
        List<Challenge> tmp = new ArrayList<>();

        // 해당 사용자의 ChallengeParticipation을 가져와서 Challenge와 매핑
        List<ChallengeParticipation> userParticipations = challengeParticipationRespository.findByUser(user)
                .orElse(Collections.emptyList());

        for (Challenge challenge : mainChallenge) {
            Optional<ChallengeParticipation> foundChallenge = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            if (foundChallenge.isEmpty()) {
                tmp.add(challenge);  // 참여하지 않은 챌린지
            } else {
                result.add(challenge);  // 참여 중인 챌린지
            }
        }

        // 참여 중인 챌린지를 ChallengeParticipation의 생성 시간(createdDate) 기준으로 정렬
        result.sort((c1, c2) -> {
            Optional<ChallengeParticipation> cp1 = challengeParticipationRespository.findByUserAndChallenge(user, c1);
            Optional<ChallengeParticipation> cp2 = challengeParticipationRespository.findByUserAndChallenge(user, c2);

            return cp1.get().getCreatedAt().compareTo(cp2.get().getCreatedAt());
        });

        // 참여하지 않은 챌린지를 뒤에 추가
        result.addAll(tmp);

        return result;
    }

    private List<ChallengeDTO> getCustomChallenge() {
        User user = userQueryService.getUser();
        List<Challenge> challenges = challengeRepository.findAll();
        challenges.removeIf(challenge -> challenge.getProducerId() == -1L);
        List<Challenge> participatingChallenge = new ArrayList<>();
        List<Challenge> notParticipatingChallenge = new ArrayList<>();
        List<Challenge> resultChallenges = new ArrayList<>();
        List<ChallengeDTO> result = new ArrayList<>();

        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cParticipation = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            if (cParticipation.isEmpty()) {
                notParticipatingChallenge.add(challenge);
                continue;
            }
            participatingChallenge.add(challenge);
        }

        resultChallenges.addAll(participatingChallenge);
        resultChallenges.addAll(notParticipatingChallenge);

        for (Challenge challenge : resultChallenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            result.add(challengeResponseDTO);
        }
        return result;
    }

    private List<ChallengeParticipation> getMyCParticipation() {
        User user = userQueryService.getUser();
        List<ChallengeParticipation> cParticipation = challengeParticipationRespository.findByUser(user).orElse(null);
        cParticipation.removeIf(cp -> !cp.isParticipating());
        List<Challenge> result = new ArrayList<>();
        return cParticipation;
    }

    public int getChallengeParticipants(ChallengeParticipation cp) {
        Challenge challenge = cp.getChallenge();
        Optional<List<ChallengeParticipation>> challengeParticipation = challengeParticipationRespository.findByChallenge(challenge);
        if (challengeParticipation.isPresent()) {
            return challengeParticipation.get().size();
        }
        return 0;
    }

}
