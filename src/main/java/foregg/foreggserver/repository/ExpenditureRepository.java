package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Expenditure;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    Optional<Expenditure> findByIdAndUser(Long id, User user);

}
