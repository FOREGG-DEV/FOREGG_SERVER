package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.SideEffectResponseDTO;
import foregg.foreggserver.dto.recordDTO.MedicalRecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.RecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.RepeatTimeResponseDTO;
import foregg.foreggserver.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class RecordConverter {

    public static Record toRecord(RecordRequestDTO dto, User user) {

        if (dto.getDate() == null) {
            return Record.builder()
                    .type(dto.getRecordType())
                    .name(dto.getName())
                    .start_date(dto.getStartDate())
                    .end_date(dto.getEndDate())
                    .repeat_date(dto.getRepeatDate())
                    .dose(dto.getDose())
                    .memo(dto.getMemo())
                    .vibration(dto.getVibration())
                    .start_end_yearmonth(DateUtil.getMonthsBetween(dto.getStartDate(), dto.getEndDate()))
                    .user(user).build();
        }
        return Record.builder()
                .type(dto.getRecordType())
                .name(dto.getName())
                .date(dto.getDate())
                .repeat_date(dto.getRepeatDate())
                .dose(dto.getDose())
                .memo(dto.getMemo())
                .vibration(dto.getVibration())
                .yearmonth(DateUtil.getYearAndMonth(dto.getDate()))
                .user(user).build();
    }

    public static MedicalRecordResponseDTO toMedicalRecordResponse(Record record) {
        List<SideEffectResponseDTO> resultList = DailyConverter.toSideEffectResponseDTO(record.getSideEffect());
        return MedicalRecordResponseDTO.builder()
                .medicalRecord(record.getMedical_record())
                .sideEffects(resultList)
                .build();
    }

    public static RecordResponseDTO toRecordResponseDTO(Record record, List<RepeatTime> repeatTimes)  {

        List<RepeatTimeResponseDTO> result = new ArrayList<>();
        for (RepeatTime repeatTime : repeatTimes) {
            RepeatTimeResponseDTO repeatTimeResponseDTO = RepeatTimeConverter.toRepeatTimeResponseDTO(repeatTime);
            result.add(repeatTimeResponseDTO);
        }

        return RecordResponseDTO.builder()
                .id(record.getId())
                .recordType(record.getType())
                .name(record.getName())
                .date(record.getDate())
                .startDate(record.getStart_date())
                .endDate(record.getEnd_date())
                .repeatDate(record.getRepeat_date())
                .repeatTimes(result)
                .dose(record.getDose())
                .memo(record.getMemo())
                .build();
    }



}
