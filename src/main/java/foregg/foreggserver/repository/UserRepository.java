package foregg.foreggserver.repository;

import foregg.foreggserver.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByKeyCode(String keyCode);

    Optional<User> findBySpouseCode(String spouseCode);

    User findByChallengeName(String challengeName);

    @Query("SELECT u FROM User u WHERE u.lastConnect <= :sevenDaysAgo AND u.lastConnect > :sevenDaysAgoMinusOneMinute")
    List<User> findUsersInactiveForSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo,
                                             @Param("sevenDaysAgoMinusOneMinute") LocalDateTime sevenDaysAgoMinusOneMinute);

}
