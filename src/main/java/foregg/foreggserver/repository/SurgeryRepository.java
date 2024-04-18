package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {

}
