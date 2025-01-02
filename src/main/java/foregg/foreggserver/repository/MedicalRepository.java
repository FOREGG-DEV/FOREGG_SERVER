package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Medical;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRepository extends JpaRepository<Medical, Long> {

    Optional<Medical> findByName(String name);

}
