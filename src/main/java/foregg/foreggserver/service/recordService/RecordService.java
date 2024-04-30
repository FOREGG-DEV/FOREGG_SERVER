package foregg.foreggserver.service.recordService;

import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.converter.RecordConverter;
import foregg.foreggserver.converter.RepeatTimeConverter;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.Schedule;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.recordDTO.MedicalRecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.MedicalRecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.RecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.RecordRepository;
import foregg.foreggserver.repository.RepeatTimeRepository;
import foregg.foreggserver.repository.UserRepository;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RecordService {

    private final RecordRepository recordRepository;
    private final RepeatTimeRepository repeatTimeRepository;
    private final UserRepository userRepository;

    //일정 추가하기
    public RecordResponseDTO addRecord(RecordRequestDTO dto) {
        User user = getUser(SecurityUtil.getCurrentUser());
        Record record = recordRepository.save(RecordConverter.toRecord(dto,user));
        List<RepeatTime> repeatTimes = dto.getRepeatTimes();
        for (RepeatTime time : repeatTimes) {
            RepeatTime repeatTime = RepeatTimeConverter.toRepeatTime(time, record);
            repeatTimeRepository.save(repeatTime);
        }
        return getRepeatTimes(record);
    }

    //일정 삭제하기
    public void deleteRecord(Long id) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        recordRepository.delete(record);
    }

    //일정 변경하기
    public void modifyRecord(Long id, RecordRequestDTO dto) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        record.updateRecord(dto);
        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if(repeatTimes.isPresent()){
            List<RepeatTime> foundRepeatTimes = repeatTimes.get();
            for (RepeatTime time : foundRepeatTimes) {
                repeatTimeRepository.delete(time);
            }
        }
        List<RepeatTime> updateTimes = dto.getRepeatTimes();
        for (RepeatTime time : updateTimes) {
            RepeatTime saveTime = RepeatTimeConverter.toRepeatTime(time, record);
            repeatTimeRepository.save(saveTime);
        }
    }

    //일정 상세 보기
    public RecordResponseDTO recordDetail(Long id) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        return getRepeatTimes(record);
    }

    //진료기록 추가하기
    public void addMedicalRecord(Long id, MedicalRecordRequestDTO dto) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        if (record.getType() != RecordType.HOSPITAL) {
            throw new RecordHandler(NOT_HOSPITAL_RECORD);
        }
        record.setMedical_record(dto.getMedicalRecord());
        recordRepository.save(record);
    }

    //진료기록 및 부작용 확인하기
    public MedicalRecordResponseDTO medicalRecordAndSideEffect(Long id) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));
        if (record.getType() != RecordType.HOSPITAL) {
            throw new RecordHandler(NOT_HOSPITAL_RECORD);
        }
        return RecordConverter.toMedicalRecordResponse(record);
    }

    //RepeatTime 추출하기
    private RecordResponseDTO getRepeatTimes(Record record) {
        Optional<List<RepeatTime>> repeatTimes = repeatTimeRepository.findByRecord(record);
        if(repeatTimes.isPresent()){
            List<RepeatTime> result = repeatTimes.get();
            return RecordConverter.toRecordResponseDTO(record, result);
        }
        return RecordConverter.toRecordResponseDTO(record, null);
    }

    private User getUser(String keycode) {
        User user = userRepository.findByKeyCode(keycode).orElseThrow(() -> new UserHandler(USER_NOT_FOUND));
        return user;
    }

    //

}
