package foregg.foreggserver.repository;

import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SideEffectRepository extends JpaRepository<SideEffect, Long> {
    Optional<List<SideEffect>> findByUser(User user);
}
