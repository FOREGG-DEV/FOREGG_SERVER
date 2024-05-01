package foregg.foreggserver.dto.homeDTO;

import foregg.foreggserver.domain.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRecordResponseDTO {

    private Long id;
    private RecordType recordType;
    private String name;
    private String memo;

}
