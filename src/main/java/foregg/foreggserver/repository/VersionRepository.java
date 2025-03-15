package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version, Long> {

    Optional<Version> findById(Long id);

}
