package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.apiPayload.exception.handler.PageHandler;
import foregg.foreggserver.converter.NotificationConverter;
import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.Reply;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO.NotificationDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.NotificationRepository;
import foregg.foreggserver.repository.ReplyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.PAGE_OUT_OF_RANGE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final ReplyRepository replyRepository;
    private final UserQueryService userQueryService;
    private final NotificationRepository notificationRepository;

    public NotificationResponseDTO getNotificationHistory(int page) {
        List<NotificationDTO> result = new ArrayList<>();

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
        result.sort(Comparator.comparing(NotificationDTO::getCreatedAt).reversed());

        // 페이징 처리
        int start = page * 15; // 시작 인덱스
        int end = Math.min(start + 15, result.size()); // 끝 인덱스

        // 인덱스 범위가 올바른지 확인
        if (start > result.size()) {
            throw new PageHandler(PAGE_OUT_OF_RANGE);
        }

        int totalElements = result.size();
        int totalPages = (int) Math.ceil((double) totalElements / 15);

        List<NotificationDTO> notificationDTOS = result.subList(start, end);

        return NotificationResponseDTO.builder()
                .dto(notificationDTOS)
                .currentPage(page)
                .totalPage(totalPages)
                .totalElements(totalElements)
                .build();
    }


}
