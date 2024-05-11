package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.challengeDTO.ChallengeAllResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;

import java.util.List;

public class ChallengeConverter {

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

    public static ChallengeMyResponseDTO toChallengeMyResponseDTO(Challenge challenge, int participants, List<String> successDays, String weekOfMonth) {
        return ChallengeMyResponseDTO.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .participants(participants)
                .successDays(successDays)
                .weekOfMonth(weekOfMonth)
                .build();
    }

    public static ChallengeParticipation toChallengeParticipation(User user, Challenge challenge) {
        return ChallengeParticipation.builder()
                .user(user)
                .challenge(challenge)
                .build();
    }
}
