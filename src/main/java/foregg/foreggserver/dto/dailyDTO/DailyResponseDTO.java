package foregg.foreggserver.dto.dailyDTO;

import foregg.foreggserver.domain.enums.DailyConditionType;
import foregg.foreggserver.domain.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyResponseDTO {

    private Long id;
    private DailyConditionType dailyConditionType;
    private String content;
    private String date;
    private EmotionType emotionType;

}
