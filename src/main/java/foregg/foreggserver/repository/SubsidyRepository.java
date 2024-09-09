package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubsidyRepository extends JpaRepository<Subsidy, Long> {

    int countByUser(User user);

    List<Subsidy> findByUserAndCount(User user, int count);

    Subsidy findByUserAndCountAndNickname(User user, int count, String nickname);

    List<Subsidy> findByUser(User user);

    Optional<Subsidy> findByIdAndUser(Long id, User user);

}
