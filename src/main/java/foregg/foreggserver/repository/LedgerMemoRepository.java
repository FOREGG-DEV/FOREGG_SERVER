package foregg.foreggserver.repository;

import foregg.foreggserver.domain.LedgerMemo;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerMemoRepository extends JpaRepository<LedgerMemo, Long> {

    LedgerMemo findByUserAndCount(User user, int count);

}
