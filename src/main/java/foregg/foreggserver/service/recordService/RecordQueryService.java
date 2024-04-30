package foregg.foreggserver.service.recordService;

import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.RecordConverter;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.ScheduleResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RecordQueryService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final RepeatTimeRepository repeatTimeRepository;

    public ScheduleResponseDTO calendar(String yearmonth) {
        List<String> adjacentMonths = DateUtil.getAdjacentMonths(yearmonth);
        User user = getUser(SecurityUtil.getCurrentUser());
        List<RecordResponseDTO> recordList = new ArrayList<>();

        for (String s : adjacentMonths) {
            Optional<List<Record>> result = recordRepository.findByUserAndYearmonth(user, s);
            if (result.isPresent()) {
                List<Record> records = result.get();
                for (Record record : records) {
                    RecordResponseDTO resultDTO = getRepeatTimes(record);
                    // 중복 체크
                    if (!recordList.stream().anyMatch(dto -> dto.getId() == resultDTO.getId())) {
                        recordList.add(resultDTO);
                    }
                }
            }
        }

        for (String s : adjacentMonths) {
            Optional<List<Record>> result = recordRepository.findByUser(user);
            if (result.isPresent()) {
                List<Record> records = result.get();
                for (Record record : records) {
                    List<String> startEndYearmonth = record.getStart_end_yearmonth();
                    RecordResponseDTO resultDTO = getRepeatTimes(record);
                    if (startEndYearmonth != null && startEndYearmonth.contains(s)) {
                        // 중복 체크
                        if (!recordList.stream().anyMatch(dto -> dto.getId() == resultDTO.getId())) {
                            recordList.add(resultDTO);
                        }
                    }
                }
            }
        }

        return ScheduleResponseDTO.builder().records(recordList).build();
    }


    private User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
    }

    private RecordResponseDTO getRepeatTimes(Record record) {
        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if(repeatTimes.isPresent()){
            List<RepeatTime> result = repeatTimes.get();
            return RecordConverter.toRecordResponseDTO(record, result);
        }
        return RecordConverter.toRecordResponseDTO(record, null);
    }


}
