package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.domain.Notification;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.fcmService.FcmService;
import foregg.foreggserver.service.recordService.RecordQueryService;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
    private final UserQueryService userQueryService;
    private final RecordQueryService recordQueryService;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, List<ScheduledFuture<?>>> scheduledTasks = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void sendDailyPush() {
        List<User> wives = userQueryService.getAllWives();

        for (User wife : wives) {
            try {
                fcmService.sendMessageTo(
                        wife.getFcmToken(),
                        "22시 하루기록 푸시 알림",
                        String.format("%s님 오늘 하루는 어떠셨나요?", wife.getNickname()),
                        "today record female",
                        null,
                        null,
                        null
                );

                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", wife.getNickname());

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.error("FCM 푸시 알림 실패: {} - 사용자의 FCM 토큰이 유효하지 않습니다. (UNREGISTERED)", wife.getNickname());
                    // 이 사용자의 FCM 토큰을 삭제하거나, 데이터베이스에서 비활성화하도록 추가 로직을 여기에 작성할 수 있습니다.
                } else {
                    log.error("FCM 푸시 알림 실패: {} - 예상치 못한 오류 발생: {}", wife.getNickname(), e.getMessage());
                }
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {} - 사용자: {}", e.getMessage(), wife.getNickname());
            } catch (Exception e) {
                log.error("예기치 않은 오류 발생: {} - 사용자: {}", e.getMessage(), wife.getNickname());
            }
        }
        log.info("10시 하루기록 푸시알림이 완료되었습니다.");
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void send8Alarm() {

        List<User> wives = userQueryService.getAllWives();
        for (User wife : wives) {
            if (recordQueryService.getUsersWithTodayHospitalAndEtcRecord(wife)) {
                try {
                    fcmService.sendMessageTo(wife.getFcmToken(), "병원일정, 기타일정 알림", "오늘의 일정을 확인해주세요","calendar_graph",null, null,null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (wife.getSpouseId() != null) {
                    Optional<User> foundHusband = userRepository.findById(wife.getSpouseId());
                    try {
                        fcmService.sendMessageTo(foundHusband.get().getFcmToken(), "병원일정, 기타일정 알림", "오늘의 일정을 확인해주세요","calendar_graph",null, null, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
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
                fcmService.sendMessageTo(user.getFcmToken(), "주사 일정 알림", String.format("%s님 %s 주사 맞을 시간이에요.",user.getNickname(),date+time), "injection female", recordId.toString(), time, null);
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

    //알림 만드는 로직
    public Notification createNotification(NotificationType notificationType, User receiver, String sender, Long targetId) {
        return Notification.builder()
                .notificationType(notificationType)
                .receiver(receiver)
                .sender(sender)
                .date(LocalDate.now().toString())
                .targetId(targetId)
                .build();
    }

}
