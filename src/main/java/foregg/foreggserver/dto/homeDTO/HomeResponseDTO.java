package foregg.foreggserver.dto.homeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponseDTO {

    private String userName;
    private String todayDate;
    private List<HomeRecordResponseDTO> homeRecordResponseDTO;

}
