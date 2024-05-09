package foregg.foreggserver.dto.dailyDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyTotalResponseDTO {
    private List<DailyResponseDTO> dailyResponseDTO;
}
