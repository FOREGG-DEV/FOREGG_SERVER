package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeAllResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
