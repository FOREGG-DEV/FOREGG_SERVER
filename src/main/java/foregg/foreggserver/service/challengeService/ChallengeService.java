package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
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
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRespository;
    private final UserQueryService userQueryService;

    public void participate(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Optional<ChallengeParticipation> challengeParticipation = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
        if (challengeParticipation.isPresent()) {
            throw new ChallengeHandler(ALREADY_PARTICIPATING);
        }
        challengeParticipationRespository.save(ChallengeConverter.toChallengeParticipation(user, challenge));
    }

    public void quitChallenge(Long challengeId) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Challenge challenge = challengeRepository.findById(challengeId).
                orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipations = challengeParticipationRespository.findByUserAndChallenge(user,challenge).
                orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        challengeParticipationRespository.delete(challengeParticipations);
    }

    public void success(Long challengeId) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        String now = DateUtil.formatLocalDateTime(LocalDate.now());
        Challenge challenge = challengeRepository.findById(challengeId).
                orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipation = challengeParticipationRespository.findByUserAndChallenge(user, challenge)
                .orElseThrow(()-> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));

        List<String> successDays = challengeParticipation.getSuccessDays();
        if (successDays == null) {
            successDays = new ArrayList<>();
            successDays.add(now);
        }else{
            if (successDays.contains(now)) {
                throw new ChallengeHandler(DUPLICATED_SUCCESS_DATE);
            }
            successDays.add(now);
        }

        challengeParticipation.setSuccessDays(successDays);
    }

}
