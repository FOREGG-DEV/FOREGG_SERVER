package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ChallengeConverter {

    public static ChallengeDTO toChallengeResponseDTO(Challenge challenge, User user, Optional<ChallengeParticipation> cp) {
        boolean isOpen;
        boolean isParticipating;
        if (cp.isEmpty()) {
            isOpen = false;
            isParticipating = false;
        }else{
            isOpen = cp.get().isOpen();
            isParticipating = cp.get().isParticipating();
        }

        int participatingCount = (int)challenge.getChallengeParticipations().stream()
                .filter(ChallengeParticipation::isParticipating) // isParticipating이 true인 객체 필터링
                .count(); // 필터링된 객체의 개수 세기

        return ChallengeDTO.builder()
                .id(challenge.getId())
                .point(user.getPoint())
                .image(challenge.getImage())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .participants(participatingCount)
                .isOpen(isOpen)
                .isParticipating(isParticipating).build();
    }

    public static MyChallengeDTO toMyChallengeDTO(ChallengeParticipation cp, int participants) {
        Challenge challenge = cp.getChallenge();
        return MyChallengeDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .participants(participants)
                .successDays(cp.getSuccessDays()).build();
    }

}
