package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Schedule;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByUserAndYearmonth(User user, String yearMonth);

}
