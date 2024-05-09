package foregg.foreggserver.dto.dailyDTO;

import foregg.foreggserver.domain.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionRequestDTO {
    private EmotionType emotionType;
}
