package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.dto.homeDTO.HomeRecordResponseDTO;
import foregg.foreggserver.dto.homeDTO.HomeResponseDTO;

import java.util.List;

public class HomeConverter {

    public static HomeRecordResponseDTO toHomeRecordResponseDTO(Record record) {
        return HomeRecordResponseDTO.builder()
                .id(record.getId())
                .recordType(record.getType())
                .name(record.getName())
                .memo(record.getMemo()).build();
    }

    public static HomeResponseDTO toHomeResponseDTO(String userName, String todayDate, List<HomeRecordResponseDTO> homeRecordResponseDTOS) {
        return HomeResponseDTO.builder()
                .userName(userName)
                .todayDate(todayDate)
                .homeRecordResponseDTO(homeRecordResponseDTOS).build();
    }
}
