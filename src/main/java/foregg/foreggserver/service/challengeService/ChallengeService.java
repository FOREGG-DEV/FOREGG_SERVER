package foregg.foreggserver.service.challengeService;

import foregg.foreggserver.apiPayload.exception.handler.ChallengeHandler;
import foregg.foreggserver.converter.ChallengeConverter;
import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import foregg.foreggserver.repository.ChallengeRepository;
import foregg.foreggserver.repository.UserRepository;
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
import static foregg.foreggserver.dto.challengeDTO.ChallengeRequestDTO.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipationRespository challengeParticipationRespository;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    public void participate(Long id) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation cp = challengeParticipationRespository.findByUserAndChallenge(user, challenge).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_OPEN));
        if (cp.isOpen()) {
            throw new ChallengeHandler(ALREADY_PARTICIPATING);
        }
        cp.setParticipating(true);
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

    public void deleteTodaySuccess(Long id) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        ChallengeParticipation challengeParticipation = challengeParticipationRespository.findByUserAndChallenge(user, challenge).
                orElseThrow(() -> new ChallengeHandler(NO_PARTICIPATING_CHALLENGE));
        List<String> successDays = challengeParticipation.getSuccessDays();
        if (successDays == null) {
            throw new ChallengeHandler(NO_SUCCESS_DAY);
        }
        successDays.remove(DateUtil.formatLocalDateTime(LocalDate.now()));
    }

    public String createChallengeName(ChallengeNameRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
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
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ChallengeHandler(CHALLENGE_NOT_FOUND));
        Optional<ChallengeParticipation> foundCp = challengeParticipationRespository.findByUserAndChallenge(user, challenge);
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
        challengeParticipationRespository.save(cp);
    }

    public void createChallenge(ChallengeCreateRequestDTO dto) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        user.deductPoint(1000);
        Challenge challenge = Challenge.builder().name(dto.getName()).description(dto.getDescription())
                .image(dto.getChallengeEmojiType())
                .producerId(user.getId())
                .build();
        challengeRepository.save(challenge);
    }


}
