package foregg.foreggserver.repository;

import foregg.foreggserver.domain.ChallengeParticipation;
import foregg.foreggserver.domain.ChallengeSuccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeSuccessRepository extends JpaRepository<ChallengeSuccess, Long> {

    Optional<ChallengeSuccess> findByDate(String date);

    Optional<ChallengeSuccess> findByChallengeParticipationAndDate(ChallengeParticipation cp, String date);

}
