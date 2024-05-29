package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.domain.User;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void sendDailyPush() {
        List<User> users = userRepository.findAll();
        List<User> wives = new ArrayList<>();
        for (User user : users) {
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                // ROLE_HUSBAND와 같은 역할을 찾으면 true 반환
                if (authority.getAuthority().equals("ROLE_WIFE")) {
                    wives.add(user);
                }
            }
        }

        for (User wife : wives) {
            try {
                fcmService.sendMessageTo(wife.getFcmToken(),
                        "22시 하루기록 푸시 알림",
                        String.format("%s님 오늘 하루는 어땠나요?", wife.getNickname()),
                        "today record female",
                        null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
