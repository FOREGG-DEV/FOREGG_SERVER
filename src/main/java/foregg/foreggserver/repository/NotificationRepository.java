package foregg.foreggserver.repository;

import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySenderAndReceiverAndDate(User sender, User receiver, String date);

    Notification findBySenderAndReceiverAndDateAndNotificationType(User sender, User receiver, String date, NotificationType notificationType);

    List<Notification> findBySenderAndDateAndNotificationType(User sender, String date, NotificationType notificationType);
}
