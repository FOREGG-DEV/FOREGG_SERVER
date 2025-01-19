package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findBySenderAndReceiverAndDateAndNotificationType(String senderNickname, User receiver, String date, NotificationType notificationType);

    List<Notification> findBySenderAndDateAndNotificationType(String senderNickname, String date, NotificationType notificationType);

    List<Notification> findByReceiver(User receiver);

}
