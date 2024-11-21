package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.Reply;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO;
import foregg.foreggserver.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static List<NotificationResponseDTO> fromReplyToNotification(List<Reply> replyList) {
        List<NotificationResponseDTO> result = new ArrayList<>();
        for (Reply reply : replyList) {
            NotificationResponseDTO dto = NotificationResponseDTO.builder()
                    .id(reply.getId())
                    .notificationType(NotificationType.REPLY)
                    .sender(reply.getSender().getNickname())
                    .elapsedTime(DateUtil.getElapsedTime(reply.getCreatedAt()))
                    .createdAt(reply.getCreatedAt().toString())
                    .build();
            result.add(dto);
        }
        return result;
    }

    public static List<NotificationResponseDTO> toNotificationResponse(List<Notification> notificationList) {
        List<NotificationResponseDTO> result = new ArrayList<>();
        for (Notification notification : notificationList) {
            NotificationResponseDTO dto = NotificationResponseDTO.builder()
                    .id(notification.getId())
                    .notificationType(notification.getNotificationType())
                    .sender(notification.getSender().getNickname())
                    .elapsedTime(DateUtil.getElapsedTime(notification.getCreatedAt()))
                    .createdAt(notification.getCreatedAt().toString())
                    .build();
            result.add(dto);
        }
        return result;
    }
}
