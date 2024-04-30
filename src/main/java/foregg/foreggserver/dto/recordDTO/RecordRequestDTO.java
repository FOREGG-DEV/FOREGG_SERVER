package foregg.foreggserver.dto.recordDTO;

import foregg.foreggserver.domain.RepeatTime;
import foregg.foreggserver.domain.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordRequestDTO {

    private RecordType recordType;
    private String name;
    private String date;
    private String startDate;
    private String endDate;
    private String repeatDate;
    private List<RepeatTime> repeatTimes;
    private String dose;
    private String memo;

}
