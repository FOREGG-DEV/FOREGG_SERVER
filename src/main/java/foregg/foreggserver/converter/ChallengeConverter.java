package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeAllResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.repository.ChallengeParticipationRespository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ChallengeConverter {

    private final ChallengeParticipationRespository challengeParticipationRespository;

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

    public static ChallengeAllResponseDTO toChallengeAllResponseDTO(Challenge challenge, int participants, boolean ifMine) {
        return ChallengeAllResponseDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .participants(participants)
                .ifMine(ifMine)
                .build();
    }

    public static ChallengeMyResponseDTO toChallengeMyResponseDTO(Challenge challenge, int participants, List<String> successDays, String weekOfMonth, boolean lastSaturday) {
        return ChallengeMyResponseDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .participants(participants)
                .successDays(successDays)
                .weekOfMonth(weekOfMonth)
                .lastSaturday(lastSaturday)
                .build();
    }

    public static ChallengeParticipation toChallengeParticipation(User user, Challenge challenge) {
        return ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .build();
    }
}
