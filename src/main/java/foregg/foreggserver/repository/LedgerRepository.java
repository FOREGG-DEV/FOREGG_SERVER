package foregg.foreggserver.repository;


import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findByUserAndCount(User user, int count);

    List<Ledger> findByUser(User user);

    Optional<Ledger> findByIdAndUser(Long id, User user);
}
