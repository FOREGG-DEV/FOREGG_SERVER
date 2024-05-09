package foregg.foreggserver.repository;

import foregg.foreggserver.domain.SideEffect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SideEffectRepository extends JpaRepository<SideEffect, Long> {
}
