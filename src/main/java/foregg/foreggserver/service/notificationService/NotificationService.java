package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, List<ScheduledFuture<?>>> scheduledTasks = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void sendDailyPush() {
        log.info("10시 하루기록 알림이 실행되었습니다.");

        List<User> users = userRepository.findAll();
        log.info("총 {}명의 사용자가 조회되었습니다.", users.size());

        List<User> wives = new ArrayList<>();
        for (User user : users) {
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_WIFE")) {
                    wives.add(user);
                }
            }
        }

        log.info("총 {}명의 ROLE_WIFE 사용자가 발견되었습니다.", wives.size());


        for (User wife : wives) {
            try {
                log.info("FCM 푸시 알림을 {}에게 보내고 있습니다.", wife.getNickname());

                fcmService.sendMessageTo(
                        wife.getFcmToken(),
                        "22시 하루기록 푸시 알림",
                        String.format("%s님 오늘 하루는 어떠셨나요?", wife.getNickname()),
                        "today record female",
                        null,
                        null
                );

                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", wife.getNickname());

            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.info("10시 하루기록 푸시알림이 완료되었습니다.");
    }


    public void scheduleNotifications(User user, Record record, List<RepeatTime> repeatTimes) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now(); // 현재 시간

        if (record.getDate() != null) {
            // 단일 날짜의 경우
            LocalDate singleDate = LocalDate.parse(record.getDate(), dateFormatter);
            for (RepeatTime repeatTime : repeatTimes) {
                LocalTime time = LocalTime.parse(repeatTime.getTime(), timeFormatter);
                LocalDateTime notificationDateTime = LocalDateTime.of(singleDate, time);
                // 현재 시간보다 이전인지 확인
                if (!notificationDateTime.isBefore(now)) {
                    scheduleNotification(user, notificationDateTime, record.getId(), repeatTime.getTime());
                }
            }
        } else if (record.getStart_date() != null && record.getEnd_date() != null) {
            // 반복 날짜의 경우
            LocalDate startDate = LocalDate.parse(record.getStart_date(), dateFormatter);
            LocalDate endDate = LocalDate.parse(record.getEnd_date(), dateFormatter);
            Set<DayOfWeek> repeatDays = parseRepeatDays(record.getRepeat_date());

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (repeatDays.contains(date.getDayOfWeek())) {
                    for (RepeatTime repeatTime : repeatTimes) {
                        LocalTime time = LocalTime.parse(repeatTime.getTime(), timeFormatter);
                        LocalDateTime notificationDateTime = LocalDateTime.of(date, time);
                        // 현재 시간보다 이전인지 확인
                        if (!notificationDateTime.isBefore(now)) {
                            scheduleNotification(user, notificationDateTime, record.getId(), repeatTime.getTime());
                        }
                    }
                }
            }
        }
    }


    private Set<DayOfWeek> parseRepeatDays(String repeatDaysStr) {
        Set<DayOfWeek> repeatDays = new HashSet<>();
        if (repeatDaysStr.equals("매일")) {
            repeatDays.addAll(Arrays.asList(DayOfWeek.values()));
        } else {
            String[] days = repeatDaysStr.split(",");
            for (String day : days) {
                switch (day.trim()) {
                    case "월":
                        repeatDays.add(DayOfWeek.MONDAY);
                        break;
                    case "화":
                        repeatDays.add(DayOfWeek.TUESDAY);
                        break;
                    case "수":
                        repeatDays.add(DayOfWeek.WEDNESDAY);
                        break;
                    case "목":
                        repeatDays.add(DayOfWeek.THURSDAY);
                        break;
                    case "금":
                        repeatDays.add(DayOfWeek.FRIDAY);
                        break;
                    case "토":
                        repeatDays.add(DayOfWeek.SATURDAY);
                        break;
                    case "일":
                        repeatDays.add(DayOfWeek.SUNDAY);
                        break;
                }
            }
        }
        return repeatDays;
    }

    public void scheduleNotification(User user, LocalDateTime notificationDateTime, Long recordId, String time) {
        Date date = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
            try {
                fcmService.sendMessageTo(user.getFcmToken(), "주사 일정 알림", String.format("%s님 %s 주사 맞을 시간이에요.",user.getNickname(),time), "injection female", recordId.toString(), time);
                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", user.getNickname());
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                e.printStackTrace();
            }
        }, date);
        scheduledTasks.computeIfAbsent(recordId, k -> new ArrayList<>()).add(scheduledFuture);
    }

    public void cancelScheduledTasks(Long recordId) {
        List<ScheduledFuture<?>> futures = scheduledTasks.remove(recordId);
        if (futures != null) {
            for (ScheduledFuture<?> future : futures) {
                future.cancel(true);
            }
        }
    }



}
