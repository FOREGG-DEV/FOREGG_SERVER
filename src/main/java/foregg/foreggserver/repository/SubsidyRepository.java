package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubsidyRepository extends JpaRepository<Subsidy, Long> {

    int countByUser(User user);

    List<Subsidy> findByUserAndCount(User user, int count);

}
