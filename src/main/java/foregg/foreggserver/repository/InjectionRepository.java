package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Injection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InjectionRepository extends JpaRepository<Injection, Long> {

    Optional<Injection> findByName(String name);

}
