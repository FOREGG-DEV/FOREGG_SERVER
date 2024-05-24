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

import java.security.Security;
import java.time.LocalDate;
import java.util.*;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class RecordQueryService {

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


    private User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
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
            return HomeConverter.toHomeResponseDTO(me.getNickname(), spouseName, todayDate, me.getSsn() ,resultList, dailyQueryService.getTodayDaily(), null);
        }
        return HomeConverter.toHomeResponseDTO(me.getNickname(),spouseName, todayDate, me.getSsn(),resultList, null, null);
    }

    public Record getNearestHospitalRecord() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Record> foundRecords = recordRepository.findByUserAndType(user, RecordType.HOSPITAL)
                .orElseThrow(() -> new RecordHandler(NOT_RESERVED_HOSPITAL_RECORD));
        List<String> dates = new ArrayList<>();

        for (Record record : foundRecords) {
            if (dates != null) {
                dates.add(record.getDate());
            }
        }

        LocalDate today = LocalDate.now();

        while (true) {
            String resultDate;
            today = today.plusDays(1);
            if (dates.contains(DateUtil.formatLocalDateTime(today))) {
                resultDate = DateUtil.formatLocalDateTime(today);
                return recordRepository.findByDateAndType(resultDate, RecordType.HOSPITAL);
            }
        }
    }


}
