package foregg.foreggserver.dto.notificationDTO;

import foregg.foreggserver.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private List<NotificationDTO> dto;
    private int currentPage;
    private int totalPage;
    private int totalElements;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationDTO {
        private Long id;
        private Long targetId;
        private NotificationType notificationType;
        private String sender;
        private String createdAt;
        private String elapsedTime;
    }

}
