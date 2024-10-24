package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeAllResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_CHALLENGE;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRespository;
    private final UserQueryService userQueryService;

    public ChallengeResponseDTO challengeMain() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<ChallengeDTO> result = new ArrayList<>();

        //일단 관리자 id는 -1
        List<Challenge> challenges = getMainChallenge();
        for (Challenge challenge : challenges) {
            log.info("챌린지 " +challenge.getId());
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            result.add(challengeResponseDTO);
        }
        return ChallengeResponseDTO.builder().dtos(result).build();
    }


    public List<ChallengeAllResponseDTO> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengeAllResponseDTO> resultList = new ArrayList<>();

        for (Challenge challenge : challenges) {
            Optional<List<ChallengeParticipation>> foundChallengeParticipation = challengeParticipationRespository.findByChallenge(challenge);
            if (foundChallengeParticipation.isEmpty()) {
                continue;
            }
            List<ChallengeParticipation> challengeParticipations = foundChallengeParticipation.get();
            int participants = challengeParticipations.size();
            boolean ifMine=false;
            Optional<ChallengeParticipation> byUserAndChallenge = challengeParticipationRespository.findByUserAndChallenge(userQueryService.getUser(SecurityUtil.getCurrentUser()), challenge);
            if (byUserAndChallenge.isPresent()) {
                ifMine = true;
            }
            resultList.add(ChallengeConverter.toChallengeAllResponseDTO(challenge,participants,ifMine));
        }
        return resultList;
    }

    public List<ChallengeMyResponseDTO> getMyChallenges() {
        List<String> weekDates = DateUtil.getWeekDates();

        List<ChallengeMyResponseDTO> resultList = new ArrayList<>();
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<ChallengeParticipation> foundChallengeParticipation = challengeParticipationRespository.findByUser(user).orElseThrow(() -> new ChallengeHandler(NOT_FOUND_MY_CHALLENGE));

        for (ChallengeParticipation result : foundChallengeParticipation) {
            boolean lastSaturday = false;
            Challenge challenge = result.getChallenge();
            List<String> successDays = result.getSuccessDays();

            if (successDays!= null && successDays.contains(DateUtil.getLastSaturday())) {
                lastSaturday = true;
            }
            Optional<List<ChallengeParticipation>> byChallenge = challengeParticipationRespository.findByChallenge(challenge);
            List<String> successDates = extractSuccessDays(weekDates, successDays);
            List<String> successDaysResult = DateUtil.convertDatesToDayOfWeek(successDates);
            ChallengeMyResponseDTO resultDTO = ChallengeConverter.toChallengeMyResponseDTO(challenge, getChallengeParticipants(byChallenge),successDaysResult, DateUtil.getWeekOfMonth(DateUtil.formatLocalDateTime(LocalDate.now())), lastSaturday);
            resultList.add(resultDTO);
        }
        return resultList;
    }

    private List<Challenge> getMainChallenge() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
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


    private int getChallengeParticipants(Optional<List<ChallengeParticipation>> challengeParticipations) {
        if (challengeParticipations.isEmpty()) {
            return 0;
        }
        return challengeParticipations.get().size();
    }

    private List<String> extractSuccessDays(List<String> weekDates, List<String> successDates) {
        List<String> resultList = new ArrayList<>();
        if (successDates == null) {
            return null;
        }
        for (String successDate : successDates) {
            if (weekDates.contains(successDate)) {
                resultList.add(successDate);
            }
        }
        return resultList;
    }

}
