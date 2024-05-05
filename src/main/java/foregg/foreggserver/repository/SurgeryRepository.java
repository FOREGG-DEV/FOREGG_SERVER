package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    Optional<Surgery> findByUser(User user);
}
