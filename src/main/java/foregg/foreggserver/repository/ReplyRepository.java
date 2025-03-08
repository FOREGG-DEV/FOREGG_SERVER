package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Reply;
import foregg.foreggserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByReceiverId(Long receiverId);

}
