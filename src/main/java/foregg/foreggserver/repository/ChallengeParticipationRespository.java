package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Challenge;
import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRespository extends JpaRepository<ChallengeParticipation, Long> {

    Optional<List<ChallengeParticipation>> findByChallenge(Challenge challenge);

    Optional<List<ChallengeParticipation>> findByUser(User user);

    Optional<ChallengeParticipation> findByUserAndChallenge(User user, Challenge challenge);

}
