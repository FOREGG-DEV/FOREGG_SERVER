package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.apiPayload.exception.handler.PageHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeParticipantDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeParticipantsDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO.MyChallengeDTO;
import foregg.foreggserver.repository.*;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final UserQueryService userQueryService;
    private final NotificationRepository notificationRepository;
    private final ChallengeSuccessRepository challengeSuccessRepository;
    private final UserRepository userRepository;

    public ChallengeResponseDTO challengeMain() {
        User user = userQueryService.getUser();
        if (user.getChallengeName() == null) {
            throw new ChallengeHandler(MAKE_NICKNAME_FIRST);
        }
        List<ChallengeDTO> result = new ArrayList<>();

        //일단 관리자 id는 -1
        List<Challenge> challenges = getMainChallenge();
        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
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
            Optional<ChallengeParticipation> cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            resultList.add(challengeResponseDTO);
        }
        resultList.addAll(getCustomChallenge());
        return ChallengeResponseDTO.builder().dtos(resultList).build();
    }

    public MyChallengeTotalDTO getMyChallenges() {
        List<MyChallengeDTO> result = new ArrayList<>();
        List<ChallengeParticipation> cp = getMyCParticipation();

        for (ChallengeParticipation challengeParticipation : cp) {
            String firstDate = DateUtil.getFirstDayOfWeek(challengeParticipation.getStartDate());
            challengeParticipation.setFirstDate(firstDate);
            List<String> successDates = getSuccessDates(challengeParticipation);
            result.add(ChallengeConverter.toMyChallengeDTO(challengeParticipation, getChallengeParticipants(challengeParticipation), successDates));
        }
        return MyChallengeTotalDTO.builder().dtos(result).build();
    }

    public ChallengeParticipantsDTO getParticipants(Long challengeId, boolean isSuccess, Pageable pageable) {
        List<ChallengeParticipantDTO> result = new ArrayList<>();

        // 기존 로직 수행
        User currentUser = userQueryService.getUser();
        Challenge challenge = isParticipating(challengeId);
        List<ChallengeParticipation> challengeParticipations = challenge.getChallengeParticipations();

        challengeParticipations.removeIf(cp -> cp.getUser().equals(currentUser));
        if (isSuccess) {
            challengeParticipations.removeIf(cp -> challengeSuccessRepository.findByChallengeParticipationAndDate(cp,LocalDate.now().toString()).isEmpty());
        } else {
            challengeParticipations.removeIf(cp -> challengeSuccessRepository.findByChallengeParticipationAndDate(cp,LocalDate.now().toString()).isPresent());
        }

        if (!challengeParticipations.isEmpty()) {
            for (ChallengeParticipation cp : challengeParticipations) {
                Notification notification;
                if (isSuccess) {
                    notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(currentUser.getChallengeName(), cp.getUser(), LocalDate.now().toString(), NotificationType.CLAP);
                } else {
                    notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(currentUser.getChallengeName(), cp.getUser(), LocalDate.now().toString(), NotificationType.SUPPORT);
                }
                boolean supported = notification != null;
                Optional<ChallengeSuccess> foundChallengeSuccess = challengeSuccessRepository.findByDate(LocalDate.now().toString());
                String comment = null;
                if (foundChallengeSuccess.isPresent()) {
                    comment = foundChallengeSuccess.get().getComment();
                }
                result.add(ChallengeConverter.toChallengeParticipantDTO(cp, supported, comment));
            }
        }

        // 페이징 처리
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), result.size());

        // 요청 범위 검증
        if (fromIndex > result.size()) {
            throw new PageHandler(PAGE_OUT_OF_RANGE);
        }

        List<ChallengeParticipantDTO> paginatedList = result.subList(fromIndex, toIndex);

        // 페이징 정보만 포함한 PageResponse 반환
        int totalPages = (int) Math.ceil((double) result.size() / pageable.getPageSize());
        return ChallengeParticipantsDTO.builder()
                .dto(paginatedList)
                .currentPage(pageable.getPageNumber())
                .totalPage(totalPages)
                .totalItems(result.size())
                .build();
    }

    //챌린지 검색 메서드
    public ChallengeResponseDTO searchChallenge(String keyword) {
        User user = userQueryService.getUser();
        List<Challenge> challenges = challengeRepository.findByNameContaining(keyword);
        List<ChallengeDTO> resultList = new ArrayList<>();
        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            resultList.add(challengeResponseDTO);
        }
        return ChallengeResponseDTO.builder().dtos(resultList).build();
    }

    public Challenge isParticipating(Long challengeId) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipation = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        if (!challengeParticipation.isParticipating()) {
            throw new ChallengeHandler((NO_PARTICIPATING_CHALLENGE));
        }
        return challenge;
    }

    public ChallengeDTO detail(Long challengeId) {
        User user = userQueryService.getUser();
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge).orElse(null);
        return ChallengeConverter.toChallengeDTO(challenge, cp, user);
    }

    public ChallengeParticipantDTO challengeSupportDetail(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        User receiver = userRepository.findById(userId).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        User sender = userQueryService.getUser();
        ChallengeParticipation challengeParticipation = challengeParticipationRepository.findByUserAndChallenge(receiver, challenge).orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        Optional<ChallengeSuccess> foundChallengeSuccess = challengeSuccessRepository.findByChallengeParticipationAndDate(challengeParticipation, LocalDate.now().toString());
        //오늘 날짜의 챌린지 성공 기록이 있는 경우
        if (foundChallengeSuccess.isPresent()) {
            Notification notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(sender.getNickname(), receiver, LocalDate.now().toString(), NotificationType.CLAP);
            boolean isSupported = true;
            if (notification == null) {
                isSupported = false;
            }
            return ChallengeParticipantDTO.builder().userId(receiver.getId()).nickname(receiver.getChallengeName()).thoughts(foundChallengeSuccess.get().getComment()).isSupported(isSupported).build();
        }
        Notification notification = notificationRepository.findBySenderAndReceiverAndDateAndNotificationType(sender.getNickname(), receiver, LocalDate.now().toString(), NotificationType.SUPPORT);
        boolean isSupported = true;
        if (notification == null) {
            isSupported = false;
        }
        return ChallengeParticipantDTO.builder().userId(receiver.getId()).nickname(receiver.getChallengeName()).thoughts(null).isSupported(isSupported).build();
    }

    private List<Challenge> getMainChallenge() {
        User user = userQueryService.getUser();
        List<Challenge> mainChallenge = challengeRepository.findByProducerId(-1L);
        List<Challenge> result = new ArrayList<>();
        List<Challenge> tmp = new ArrayList<>();

        // 해당 사용자의 ChallengeParticipation을 가져와서 Challenge와 매핑
        List<ChallengeParticipation> userParticipations = challengeParticipationRepository.findByUser(user)
                .orElse(Collections.emptyList());

        for (Challenge challenge : mainChallenge) {
            Optional<ChallengeParticipation> foundChallenge = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
            if (foundChallenge.isEmpty()) {
                tmp.add(challenge);  // 참여하지 않은 챌린지
            } else {
                result.add(challenge);  // 참여 중인 챌린지
            }
        }

        // 참여 중인 챌린지를 ChallengeParticipation의 생성 시간(createdDate) 기준으로 정렬
        result.sort((c1, c2) -> {
            Optional<ChallengeParticipation> cp1 = challengeParticipationRepository.findByUserAndChallenge(user, c1);
            Optional<ChallengeParticipation> cp2 = challengeParticipationRepository.findByUserAndChallenge(user, c2);

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
            Optional<ChallengeParticipation> cParticipation = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
            if (cParticipation.isEmpty()) {
                notParticipatingChallenge.add(challenge);
                continue;
            }
            participatingChallenge.add(challenge);
        }

        resultChallenges.addAll(participatingChallenge);
        resultChallenges.addAll(notParticipatingChallenge);

        for (Challenge challenge : resultChallenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRepository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            result.add(challengeResponseDTO);
        }
        return result;
    }

    private List<ChallengeParticipation> getMyCParticipation() {
        User user = userQueryService.getUser();
        List<ChallengeParticipation> cParticipation = challengeParticipationRepository.findByUser(user).orElse(null);
        cParticipation.removeIf(cp -> !cp.isParticipating());
        return cParticipation;
    }

    public int getChallengeParticipants(ChallengeParticipation cp) {
        Challenge challenge = cp.getChallenge();
        Optional<List<ChallengeParticipation>> challengeParticipation = challengeParticipationRepository.findByChallenge(challenge);
        if (challengeParticipation.isPresent()) {
            return challengeParticipation.get().size();
        }
        return 0;
    }

    private List<String> getSuccessDates(ChallengeParticipation challengeParticipation) {
        List<String> intervalDates = DateUtil.getIntervalDates(challengeParticipation.getFirstDate());
        List<String> result = new ArrayList<>();
        for (String date : intervalDates) {
            log.info("date"+date);
            Optional<ChallengeSuccess> foundElement = challengeSuccessRepository.findByChallengeParticipationAndDate(challengeParticipation, date);
            if (foundElement.isPresent()) {
                result.add(date);
            }
        }
        return result;
    }

}
