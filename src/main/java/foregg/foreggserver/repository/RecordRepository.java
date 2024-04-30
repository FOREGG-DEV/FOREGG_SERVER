package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    Optional<List<Record>> findByUserAndYearmonth(User user, String yearmonth);

    Optional<List<Record>> findByUser(User user);

}
