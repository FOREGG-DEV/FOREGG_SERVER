package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.dto.recordDTO.RepeatTimeResponseDTO;

public class RepeatTimeConverter {

    public static RepeatTime toRepeatTime(RepeatTime time, Record record) {
        return RepeatTime.builder()
                .time(time.getTime())
                .record(record)
                .build();
    }

    public static RepeatTimeResponseDTO toRepeatTimeResponseDTO(RepeatTime repeatTime) {
        return RepeatTimeResponseDTO.builder()
                .time(repeatTime.getTime())
                .build();
    }
}
