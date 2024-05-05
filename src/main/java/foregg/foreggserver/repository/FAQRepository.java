package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Board;
import foregg.foreggserver.domain.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface FAQRepository extends JpaRepository<FAQ, Long> {

    List<FAQ> findByQuestionContaining(String keyword);
}
