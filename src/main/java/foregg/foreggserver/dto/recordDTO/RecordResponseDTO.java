package foregg.foreggserver.dto.recordDTO;

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
public class RecordResponseDTO {

    private Long id;
    private RecordType recordType;
    private String name;
    private String date;
    private String startDate;
    private String endDate;
    private String repeatDate;
    private List<RepeatTimeResponseDTO> repeatTimes;
    private String dose;
    private String memo;

}
