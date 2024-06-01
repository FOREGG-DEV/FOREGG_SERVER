package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.enums.DailyConditionType;
import foregg.foreggserver.dto.homeDTO.HomeRecordResponseDTO;
import foregg.foreggserver.dto.homeDTO.HomeResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class HomeConverter {

    public static HomeRecordResponseDTO toHomeRecordResponseDTO(Record record) {

        List<String> resultList = new ArrayList<>();
        List<RepeatTime> repeatTimes = record.getRepeatTimes();
        for (RepeatTime rt : repeatTimes) {
            resultList.add(rt.getTime());
        }

        return HomeRecordResponseDTO.builder()
                .id(record.getId())
                .recordType(record.getType())
                .times(resultList)
                .name(record.getName())
                .memo(record.getMemo()).build();
    }

    public static HomeResponseDTO toHomeResponseDTO(String userName,
                                                    String spouseName,
                                                    String todayDate,
                                                    String ssn,
                                                    List<HomeRecordResponseDTO> homeRecordResponseDTOS,
                                                    Daily daily,
                                                    Record hospitalRecord) {

        String dailyContent = null;
        DailyConditionType type = null;

        String medicalRecord = null;
        Long medicalRecordId = null;

        if (daily != null) {
            dailyContent = daily.getContent();
            type = daily.getDailyConditionType();
        }

        if (hospitalRecord != null) {
            medicalRecord = hospitalRecord.getMedical_record();
            medicalRecordId = hospitalRecord.getId();
        }

        return HomeResponseDTO.builder()
                .userName(userName)
                .spouseName(spouseName)
                .todayDate(todayDate)
                .ssn(ssn)
                .homeRecordResponseDTO(homeRecordResponseDTOS)
                .dailyConditionType(type)
                .dailyContent(dailyContent)
                .latestMedicalRecord(medicalRecord)
                .medicalRecordId(medicalRecordId)
                .build();
    }

}
