package foregg.foreggserver.repository;

import foregg.foreggserver.domain.SpouseCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpouseCodeRepository extends JpaRepository<SpouseCode, Long> {

    Optional<SpouseCode> findByCode(String code);

}
