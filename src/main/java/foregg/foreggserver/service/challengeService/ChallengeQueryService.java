package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_MY_CHALLENGE;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRespository;
    private final UserQueryService userQueryService;

    public List<ChallengeResponseDTO> getAllChallenges() {
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengeResponseDTO> resultList = new ArrayList<>();

        for (Challenge challenge : challenges) {
            Optional<List<ChallengeParticipation>> foundChallengeParticipation = challengeParticipationRespository.findByChallenge(challenge);
            if (foundChallengeParticipation.isEmpty()) {
                continue;
            }
            List<ChallengeParticipation> challengeParticipations = foundChallengeParticipation.get();
            int participants = challengeParticipations.size();
            resultList.add(ChallengeConverter.toChallengeResponseDTO(challenge,participants));
        }
        return resultList;
    }

    public List<ChallengeResponseDTO> getMyChallenges() {
        List<String> weekDates = DateUtil.getWeekDates();

        List<ChallengeResponseDTO> resultList = new ArrayList<>();
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<ChallengeParticipation> foundChallengeParticipation = challengeParticipationRespository.findByUser(user).orElseThrow(() -> new ChallengeHandler(NOT_FOUND_MY_CHALLENGE));

        for (ChallengeParticipation result : foundChallengeParticipation) {
            Challenge challenge = result.getChallenge();
            List<String> successDays = result.getSuccessDays();
            Optional<List<ChallengeParticipation>> byChallenge = challengeParticipationRespository.findByChallenge(challenge);

            List<String> successDaysResult = extractSuccessDays(successDays, weekDates);

            ChallengeResponseDTO resultDTO = ChallengeConverter.toChallengeResponseDTO(challenge, getChallengeParticipants(byChallenge),successDaysResult);
            resultList.add(resultDTO);
        }
        return resultList;
    }

    private int getChallengeParticipants(Optional<List<ChallengeParticipation>> challengeParticipations) {
        if (challengeParticipations.isEmpty()) {
            return 0;
        }
        return challengeParticipations.get().size();
    }

    private List<String> extractSuccessDays(List<String> original, List<String> target) {
        List<String> resultList = new ArrayList<>();
        if (original == null) {
            return null;
        }
        for (String s : target) {
            if (original.contains(s)) {
                resultList.add(s);
            }
        }
        return resultList;
    }

}
