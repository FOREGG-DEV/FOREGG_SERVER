package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DailyRepository extends JpaRepository<Daily, Long> {

    Optional<Daily> findByUserAndDate(User user, String date);

    Optional<Daily> findByIdAndUser(Long id, User user);

    Optional<List<Daily>> findByUser(User user);

    Optional<List<Daily>> findByUserAndCount(User user, int count);

    @Query("SELECT d FROM Daily d WHERE d.date LIKE CONCAT(:date, '%') AND d.user = :user")
    Daily findByDateAndUser(@Param("date") String date, @Param("user") User user);  // @Param 추가 및 CONCAT 사용
}
