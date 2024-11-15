package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeDTO;
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
import java.util.*;
import java.util.stream.Collectors;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.MAKE_NICKNAME_FIRST;
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
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
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

    public List<MyChallengeDTO> getMyChallenges() {
        List<MyChallengeDTO> result = new ArrayList<>();
        List<ChallengeParticipation> cp = getMyCParticipation();
        for (ChallengeParticipation challengeParticipation : cp) {
            result.add(ChallengeConverter.toMyChallengeDTO(challengeParticipation, getChallengeParticipants(challengeParticipation)));
        }
        return result;
    }

    //챌린지 검색 메서드
    public ChallengeResponseDTO searchChallenge(String keyword) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Challenge> challenges = challengeRepository.findByNameContaining(keyword);
        List<ChallengeDTO> resultList = new ArrayList<>();
        for (Challenge challenge : challenges) {
            Optional<ChallengeParticipation> cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
            ChallengeDTO challengeResponseDTO = ChallengeConverter.toChallengeResponseDTO(challenge, user, cp);
            resultList.add(challengeResponseDTO);
        }
        return ChallengeResponseDTO.builder().dtos(resultList).build();
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

    private List<ChallengeDTO> getCustomChallenge() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
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
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
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
