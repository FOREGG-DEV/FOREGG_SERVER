package foregg.foreggserver.dto.dailyDTO;

import foregg.foreggserver.domain.enums.DailyConditionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRequestDTO {

    private DailyConditionType dailyConditionType;
    private String content;
}
