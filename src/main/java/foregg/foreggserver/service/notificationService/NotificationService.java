package foregg.foreggserver.service.notificationService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.AwakeMessageType;
import foregg.foreggserver.domain.enums.NavigationType;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.repository.*;
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

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_REPEAT_TIME;
import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final UserQueryService userQueryService;
    private final RecordRepository recordRepository;
    private final DailyRepository dailyRepository;
    private final RecordQueryService recordQueryService;
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final RepeatTimeRepository repeatTimeRepository;
    private final Map<Long, List<ScheduledFuture<?>>> scheduledTasks = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 21 * * MON,TUE,WED,THU,SAT,SUN", zone = "Asia/Seoul")
    public void sendDailyPush() {

        String title = "오후 9시 매일(금요일 제외) 데일리허그";
        String body = "%s님 오늘 하루는 어떠셨나요?";

        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            title = "오후 9시, 금요일";
            body = "%s님 오늘 하루 어떠셨나요? 스페셜 질문도 확인해보세요!";
        }

        List<User> wives = userQueryService.getAllWives();
        for (User wife : wives) {
            String navigation = NavigationType.daily_hugg_graph.toString();
            Optional<Daily> foundDaily = dailyRepository.findByUserAndDate(wife, LocalDate.now().toString());
            if (foundDaily.isEmpty()) {
                navigation = NavigationType.create_daily_hugg.toString();
            }
            try {
                fcmService.sendMessageTo(
                        wife.getFcmToken(),
                        title,
                        String.format(body, wife.getNickname()),
                        navigation,
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
                    fcmService.sendMessageTo(wife.getFcmToken(), "병원일정, 기타일정 알림", "오늘의 일정을 확인해주세요",NavigationType.calendar_graph.toString(),null, null,null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (wife.getSpouseId() != null) {
                    Optional<User> foundHusband = userRepository.findById(wife.getSpouseId());
                    try {
                        fcmService.sendMessageTo(foundHusband.get().getFcmToken(), "병원일정, 기타일정 알림", "오늘의 일정을 확인해주세요",NavigationType.calendar_graph.toString(),null, null, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void urgeReplyAlarm() {
        List<User> wives = userQueryService.getAllWives();
        for (User wife : wives) {
            if (wife.getSpouseId() == null || userRepository.findById(wife.getSpouseId()).isEmpty()) {
                continue;
            }
            User spouse = userRepository.findById(wife.getSpouseId()).orElse(null);
            Optional<Daily> foundDaily = dailyRepository.findByUserAndDate(wife, LocalDate.now().toString());
            if (foundDaily.isPresent() && foundDaily.get().getReply() == null) {
                try {
                    fcmService.sendMessageTo(spouse.getFcmToken(), "11시까지 데일리 허그 답장이 없을 경우 알림", String.format("%s님 데일리 허그에서 %s님이 애타게 기다리고 있어요!", spouse.getNickname(),wife.getNickname()), NavigationType.reply_daily_hugg.toString() +"/"+LocalDate.now(), null, null, null);
                    log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", spouse.getNickname());
                } catch (IOException e) {
                    log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    public void urgeParticipateChallengeAlarm() {
        List<User> wives = userQueryService.getAllWives();
        for (User wife : wives) {
            Optional<List<ChallengeParticipation>> cp = challengeParticipationRepository.findByUser(wife);
            if (cp.isEmpty()) {
                continue;
            }
            try {
                fcmService.sendMessageTo(wife.getFcmToken(), "참여중인 챌린지가 있는 경우, 오후 2시", "오늘의 챌린지에 참여하고, 포인트 받아가세요!", NavigationType.myChallenge.toString(), null, null, null);
                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", wife.getNickname());
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Scheduled(cron = "0 0 22 * * ?") // 매일 22시 0분에 실행
    public void notifyInactiveUsers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime sevenDaysAgoMinusOneMinute = sevenDaysAgo.minusMinutes(1);

        // 정확히 7일이 지난 사용자 찾기
        List<User> inactiveUsers = userRepository.findUsersInactiveForSevenDays(sevenDaysAgo, sevenDaysAgoMinusOneMinute);

        //알림 전송
        for (User user : inactiveUsers) {

            String body;
            String navigation;

            String[] randomMessagePair = AwakeMessageType.getRandomMessagePair();
            body = randomMessagePair[0];
            navigation = randomMessagePair[1];

            if (body.equals("MESSAGE4")) {
                body = "님 허그와 함께 건강한 생활습관 만들어요!";
                if (user.getSurgery() != null) {
                    navigation = NavigationType.myChallenge.toString();
                } else {
                    navigation = NavigationType.home_graph.toString();
                }
            }

            try {
                fcmService.sendMessageTo(user.getFcmToken(), "미접속 일주일 경과 알림", body, navigation, null, null, null);
                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", user.getNickname());
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void scheduleNotifications(User user, Record record, List<RepeatTime> repeatTimes) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now(); // 현재 시간

        //병원 일정일 경우
        if (record.getType().equals(RecordType.HOSPITAL)) {
            List<RepeatTime> foundRepeatTimes = repeatTimeRepository.findByRecord(record).orElseThrow(() -> new RecordHandler(NOT_REPEAT_TIME));
            RepeatTime repeatTime = foundRepeatTimes.get(0);
            LocalTime time = LocalTime.parse(repeatTime.getTime(), timeFormatter);
            LocalDateTime notificationDateTime = LocalDateTime.of(LocalDate.parse(record.getDate(), dateFormatter), time);
            if (!notificationDateTime.isBefore(now)) {
                scheduleNotification(user, notificationDateTime.plusHours(3), record.getId(), repeatTime.getTime());
            }
        }
        //약, 주사 기록일 경우
        else{
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
        Record record = recordRepository.findById(recordId).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        RecordType type = record.getType();
        if (type.equals(RecordType.MEDICINE)) {
            setTaskScheduler(user, notificationDateTime, recordId, time, record);
        } else if (type.equals(RecordType.INJECTION)) {
            setTaskScheduler(user, notificationDateTime, recordId, time, record);
        } else{
            setTaskAccountScheduler(user, notificationDateTime, recordId, time);
        }
    }

    public void setTaskScheduler(User user, LocalDateTime notificationDateTime, Long recordId, String time, Record record) {
        Date date = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
            try {
                fcmService.sendMessageTo(user.getFcmToken(), "약, 주사 일정이 있을 때", String.format("%s에 일정이 있어요.",date+time), NavigationType.inj_med_info_screen.toString()+record.getType(), recordId.toString(), time, record.getVibration());
                log.info("FCM 푸시 알림이 성공적으로 {}에게 전송되었습니다.", user.getNickname());
            } catch (IOException e) {
                log.error("FCM 푸시 알림을 보내는 도중 오류 발생: {}", e.getMessage());
                e.printStackTrace();
            }
        }, date);
        scheduledTasks.computeIfAbsent(recordId, k -> new ArrayList<>()).add(scheduledFuture);
    }

    public void setTaskAccountScheduler(User user, LocalDateTime notificationDateTime, Long recordId, String time) {
        Date date = Date.from(notificationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
            try {
                fcmService.sendMessageTo(user.getFcmToken(), "병원 일정 3시간 후 가계부 알림", "오늘의 소비를 기록해보세요.", NavigationType.account_graph.toString(), recordId.toString(), time, null);
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
