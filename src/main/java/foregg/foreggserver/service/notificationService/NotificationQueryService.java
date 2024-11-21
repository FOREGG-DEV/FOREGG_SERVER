package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.converter.NotificationConverter;
import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.Reply;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.NotificationRepository;
import foregg.foreggserver.repository.ReplyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final ReplyRepository replyRepository;
    private final UserQueryService userQueryService;
    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDTO> getNotificationHistory() {

        List<NotificationResponseDTO> result = new ArrayList<>();

        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(30);
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());

        // 필터링된 Reply 리스트
        List<Reply> replyList = replyRepository.findByReceiver(user).stream()
                .filter(reply -> reply.getCreatedAt().isAfter(thresholdDate))
                .toList();

        // 필터링된 Notification 리스트
        List<Notification> notificationList = notificationRepository.findByReceiver(user).stream()
                .filter(notification -> notification.getCreatedAt().isAfter(thresholdDate))
                .toList();

        // 결과 생성
        result.addAll(NotificationConverter.fromReplyToNotification(replyList));
        result.addAll(NotificationConverter.toNotificationResponse(notificationList));

        // createdAt 순서로 정렬
        result.sort(Comparator.comparing(NotificationResponseDTO::getCreatedAt).reversed());

        return result;
    }

}
