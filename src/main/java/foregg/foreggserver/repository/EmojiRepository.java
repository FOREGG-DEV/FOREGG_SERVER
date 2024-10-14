package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {

}
