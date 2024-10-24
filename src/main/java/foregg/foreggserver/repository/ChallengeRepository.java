package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAll();

    List<Challenge> findByProducerId(Long producerId);

}
