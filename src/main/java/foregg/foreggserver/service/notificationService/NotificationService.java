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
                        null,
                        null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void scheduleNotifications(User user, Record record, List<RepeatTime> repeatTimes) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (record.getDate() != null) {
            // 단일 날짜의 경우
            LocalDate singleDate = LocalDate.parse(record.getDate(), dateFormatter);
            for (RepeatTime repeatTime : repeatTimes) {
                LocalTime time = LocalTime.parse(repeatTime.getTime(), timeFormatter);
                LocalDateTime notificationDateTime = LocalDateTime.of(singleDate, time);
                scheduleNotification(user, notificationDateTime, record.getId(), repeatTime.getTime());
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
                        scheduleNotification(user, notificationDateTime, record.getId(), repeatTime.getTime());
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
                fcmService.sendMessageTo(user.getFcmToken(), "주사 일정 알림", "주사 맞을 시간입니다 ", "injection female", recordId.toString(), time);
                log.info(String.format("recordId는 %s이고 time은 %s입니다", recordId, time));
            } catch (IOException e) {
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
