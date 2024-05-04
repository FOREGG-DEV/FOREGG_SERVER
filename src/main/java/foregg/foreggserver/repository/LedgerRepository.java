package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Ledger;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    Optional<List<Ledger>> findByUser(User user);

}
