package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyRepository extends JpaRepository<Daily, Long> {

    Optional<Daily> findByUserAndDate(User user, String date);

    Optional<Daily> findByIdAndUser(Long id, User user);

    Optional<List<Daily>> findByUser(User user);
}
