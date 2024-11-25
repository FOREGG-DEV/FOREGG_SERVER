package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.Reply;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO.NotificationDTO;
import foregg.foreggserver.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static List<NotificationDTO> fromReplyToNotification(List<Reply> replyList) {
        List<NotificationDTO> result = new ArrayList<>();
        for (Reply reply : replyList) {
            NotificationDTO dto = NotificationDTO.builder()
                    .id(reply.getId())
                    .targetId(reply.getDaily().getId())
                    .notificationType(NotificationType.REPLY)
                    .sender(reply.getSender().getNickname())
                    .elapsedTime(DateUtil.getElapsedTime(reply.getCreatedAt()))
                    .createdAt(reply.getCreatedAt().toString())
                    .build();
            result.add(dto);
        }
        return result;
    }

    public static List<NotificationDTO> toNotificationResponse(List<Notification> notificationList) {
        List<NotificationDTO> result = new ArrayList<>();
        for (Notification notification : notificationList) {
            NotificationDTO dto = NotificationDTO.builder()
                    .id(notification.getId())
                    .targetId(notification.getChallengeId())
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
