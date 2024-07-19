package foregg.foreggserver.service.recordService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.HomeConverter;
import foregg.foreggserver.converter.RecordConverter;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.homeDTO.HomeRecordResponseDTO;
import foregg.foreggserver.dto.homeDTO.HomeResponseDTO;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.ScheduleResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class RecordQueryService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final RepeatTimeRepository repeatTimeRepository;
    private final UserQueryService userQueryService;
    private final DailyQueryService dailyQueryService;

    public ScheduleResponseDTO calendar(String yearmonth) {
        //인접 월
        List<String> adjacentMonths = DateUtil.getAdjacentMonths(yearmonth);
        User user = userQueryService.returnWifeOrHusband();
        List<RecordResponseDTO> resultList = new ArrayList<>();

        Optional<List<Record>> foundRecords = recordRepository.findByUser(user);
        if (foundRecords.isEmpty()) {
            return null;
        }

        List<Record> records = foundRecords.get();
        for (Record record : records) {
            if (record.getDate() == null) {
                //반복 주기가 설정되어 있는 일정
                for (String s : adjacentMonths) {
                    if (record.getStart_end_yearmonth().contains(s)) {
                        //시작 연월부터 끝 연월까지 인접 연월을 포함하고 있다면
                        resultList.add(includeRepeatTimes(record));
                        break;
                    }
                }

            }else{
                //반복 주기가 설정되어 있지 않은 일정
                if (adjacentMonths.contains(record.getYearmonth())) {
                    //인접 월에 yearmonth가 포함
                    resultList.add(includeRepeatTimes(record));
                }
            }
        }

        return ScheduleResponseDTO.builder().records(resultList).build();
    }

    private RecordResponseDTO includeRepeatTimes(Record record) {
        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if (repeatTimes.isPresent()) {
            List<RepeatTime> result = repeatTimes.get();
            return RecordConverter.toRecordResponseDTO(record, result);
        }
        return RecordConverter.toRecordResponseDTO(record, null);
    }

    public HomeResponseDTO getTodayRecord() {
        User me = userQueryService.getUser(SecurityUtil.getCurrentUser());
        User user = userQueryService.returnWifeOrHusband();
        User spouse = userQueryService.returnSpouse();
        Optional<List<Record>> foundRecord = recordRepository.findByUser(user);
        String todayDate = DateUtil.formatLocalDateTime(LocalDate.now());

        List<HomeRecordResponseDTO> resultList = new ArrayList<>();

        if (foundRecord.isEmpty()) {
            return null;
        }

        List<Record> records = foundRecord.get();
        for (Record record : records) {
            if (record.getDate() == null) {
                //반복주기가 설정된 일정
                List<String> intervalDates = DateUtil.getIntervalDates(record.getStart_date(), record.getEnd_date());
                if (intervalDates.contains(todayDate)&& ((record.getRepeat_date().contains(DateUtil.getKoreanDayOfWeek(todayDate)))|| record.getRepeat_date().contains("매일"))) {
                    resultList.add(HomeConverter.toHomeRecordResponseDTO(record));
                }
            } else {
                //반복주기가 설정되지 않은 일정
                if (record.getDate().equals(todayDate)) {
                    resultList.add(HomeConverter.toHomeRecordResponseDTO(record));
                }
            }
        }

        String spouseName = null;
        if (spouse != null) {
            spouseName = spouse.getNickname();
        }

        if (SecurityUtil.ifCurrentUserIsHusband()) {
            Record latestHospitalRecord = getTodayHospitalRecord(spouse);
            return HomeConverter.toHomeResponseDTO(me.getNickname(), spouseName, todayDate, me.getSsn() ,resultList, dailyQueryService.getTodayDaily(spouse), latestHospitalRecord);
        }
        return HomeConverter.toHomeResponseDTO(me.getNickname(),spouseName, todayDate, me.getSsn(),resultList, null, null);
    }

    public Record getTodayHospitalRecord(User user) {
        String today = DateUtil.formatLocalDateTime(LocalDate.now());

        Optional<List<Record>> recordList = recordRepository.findByDateAndTypeAndUser(today, RecordType.HOSPITAL, user);
        if (recordList.isEmpty()) {
            return null;
        }
        List<Record> foundRecord = recordList.get();

        // 가장 최근의 Record를 찾
        Optional<Record> latestRecord = foundRecord.stream()
                .filter(record -> !record.getRepeatTimes().isEmpty())
                .max(Comparator.comparing(record -> record.getRepeatTimes().stream()
                        .map(repeatTime -> LocalTime.parse(repeatTime.getTime(), TIME_FORMATTER))
                        .max(Comparator.naturalOrder())
                        .orElse(LocalTime.MIN)));

        // 최신 Record가 medical_record를 가지고 있지 않다면, 다음으로 최근의 Record 중 medical_record를 가지고 있는 Record를 찾
        if (latestRecord.isPresent() && (latestRecord.get().getMedical_record() == null || latestRecord.get().getMedical_record().isEmpty())) {
            return foundRecord.stream()
                    .filter(record -> !record.getRepeatTimes().isEmpty() && record.getMedical_record() != null && !record.getMedical_record().isEmpty())
                    .max(Comparator.comparing(record -> record.getRepeatTimes().stream()
                            .map(repeatTime -> LocalTime.parse(repeatTime.getTime(), TIME_FORMATTER))
                            .max(Comparator.naturalOrder())
                            .orElse(LocalTime.MIN)))
                    .orElse(null);
        }
        return latestRecord.orElse(null);
    }

    public Record getNearestHospitalRecord(LocalDate today) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());

        List<Record> foundRecords = recordRepository.findByUserAndType(user, RecordType.HOSPITAL)
                .orElseThrow(() -> new RecordHandler(NOT_RESERVED_HOSPITAL_RECORD));

        // 오늘 날짜 이후의 단순 날짜 기록을 필터링하고 날짜로 정렬합니다.
        List<Record> upcomingDateRecords = foundRecords.stream()
                .filter(record -> record.getDate() != null)
                .filter(record -> LocalDate.parse(record.getDate(), DATE_FORMATTER).isAfter(today))
                .sorted(Comparator.comparing(record -> LocalDate.parse(record.getDate(), DATE_FORMATTER)))
                .toList();

        // 반복 기록을 처리하여 Record와 날짜의 쌍으로 반환합니다.
        List<Record> upcomingRepeatRecords = foundRecords.stream()
                .filter(record -> record.getDate() == null)
                .filter(record -> record.getStart_date() != null && record.getEnd_date() != null)
                .flatMap(record -> getNextOccurrences(record.getStart_date(), record.getEnd_date(), record.getRepeat_date())
                        .map(date -> new AbstractMap.SimpleEntry<>(record, date)))
                .filter(entry -> entry.getValue().isAfter(today))
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();

        // 단순 날짜 기록과 반복 기록을 합쳐서 가장 가까운 기록을 찾습니다.
        List<Record> allUpcomingRecords = Stream.concat(upcomingDateRecords.stream(), upcomingRepeatRecords.stream())
                .sorted(Comparator.comparing(record -> {
                    LocalDate date = record.getDate() != null
                            ? LocalDate.parse(record.getDate(), DATE_FORMATTER)
                            : getNextOccurrences(record.getStart_date(), record.getEnd_date(), record.getRepeat_date())
                            .filter(d -> d.isAfter(today))
                            .findFirst()
                            .orElse(LocalDate.MAX);
                    return date.atTime(record.getRepeatTimes() != null
                            ? record.getRepeatTimes().stream()
                            .map(repeatTime -> LocalTime.parse(repeatTime.getTime(), TIME_FORMATTER))
                            .min(Comparator.naturalOrder())
                            .orElse(LocalTime.MAX)
                            : LocalTime.MAX);
                }))
                .toList();

        return allUpcomingRecords.stream().findFirst().orElse(null);
    }

    // 다음 발생일들을 계산하는 헬퍼 메서드
    private Stream<LocalDate> getNextOccurrences(String startDate, String endDate, String repeatDate) {
        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        List<DayOfWeek> repeatDays;

        if ("매일".equals(repeatDate)) {
            repeatDays = Arrays.asList(DayOfWeek.values());
        } else {
            repeatDays = Arrays.stream(repeatDate.split(","))
                    .map(String::trim)
                    .map(this::parseDayOfWeek)
                    .toList();
        }

        LocalDate nextOccurrence = start;
        List<LocalDate> occurrences = new ArrayList<>();

        while (nextOccurrence.isBefore(end) || nextOccurrence.equals(end)) {
            if (repeatDays.contains(nextOccurrence.getDayOfWeek())) {
                occurrences.add(nextOccurrence);
            }
            nextOccurrence = nextOccurrence.plusDays(1);
        }

        return occurrences.stream();
    }

    // 요일 문자열을 DayOfWeek로 변환하는 헬퍼 메서드
    private DayOfWeek parseDayOfWeek(String day) {
        switch (day) {
            case "월": return DayOfWeek.MONDAY;
            case "화": return DayOfWeek.TUESDAY;
            case "수": return DayOfWeek.WEDNESDAY;
            case "목": return DayOfWeek.THURSDAY;
            case "금": return DayOfWeek.FRIDAY;
            case "토": return DayOfWeek.SATURDAY;
            case "일": return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    public Record getNearestDateTime(Record record1, Record record2) {
        LocalDateTime dateTime1 = getNearestRecordDateTime(record1, LocalDate.now());
        LocalDateTime dateTime2 = getNearestRecordDateTime(record2, LocalDate.now());

        return dateTime1.isBefore(dateTime2) ? record1 : record2;
    }

    private LocalDateTime getNearestRecordDateTime(Record record, LocalDate today) {
        if (record.getDate() != null) {
            // 단순 기록의 날짜와 시간을 반환
            LocalDate date = LocalDate.parse(record.getDate(), DATE_FORMATTER);
            if (date.isAfter(today)) {
                LocalTime time = getEarliestRepeatTime(record);
                return date.atTime(time != null ? time : LocalTime.MIN);
            }
        } else if (record.getStart_date() != null && record.getEnd_date() != null && record.getRepeat_date() != null) {
            // 반복 기록의 가장 가까운 날짜와 시간을 반환
            return getNextOccurrences(record.getStart_date(), record.getEnd_date(), record.getRepeat_date())
                    .filter(d -> d.isAfter(today))
                    .map(d -> d.atTime(Optional.ofNullable(getEarliestRepeatTime(record)).orElse(LocalTime.MIN)))
                    .findFirst()
                    .orElse(LocalDateTime.MAX);
        }
        return LocalDateTime.MAX;
    }

    private LocalTime getEarliestRepeatTime(Record record) {
        if (record.getRepeatTimes() != null && !record.getRepeatTimes().isEmpty()) {
            return record.getRepeatTimes().stream()
                    .map(repeatTime -> LocalTime.parse(repeatTime.getTime(), TIME_FORMATTER))
                    .min(Comparator.naturalOrder())
                    .orElse(LocalTime.MAX);
        }
        return null;
    }

}
