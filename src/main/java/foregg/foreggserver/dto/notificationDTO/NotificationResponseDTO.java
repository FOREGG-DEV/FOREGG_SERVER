package foregg.foreggserver.dto.notificationDTO;

import foregg.foreggserver.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private NotificationType notificationType;
    private String sender;
    private String createdAt;
    private String elapsedTime;

}
