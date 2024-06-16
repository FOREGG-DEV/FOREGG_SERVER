package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Information;
import foregg.foreggserver.domain.enums.InformationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InformationRepository extends JpaRepository<Information, Long> {

    Optional<List<Information>> findByInformationType(InformationType informationType);

}
