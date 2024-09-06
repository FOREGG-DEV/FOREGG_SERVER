package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
