package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepeatTimeRepository extends JpaRepository<RepeatTime, Long> {

    Optional<List<RepeatTime>> findByRecord(Record record);

    Optional<RepeatTime> findByRecordAndTime(Record record, String time);

}
