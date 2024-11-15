package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findByDate(String date);

    List<Question> findByDateIsNull();

    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.date = null")
    void updateAllDatesToNull();

    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomQuestion();
}
