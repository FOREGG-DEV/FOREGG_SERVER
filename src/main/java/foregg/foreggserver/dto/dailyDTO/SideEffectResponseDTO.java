package foregg.foreggserver.dto.dailyDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SideEffectResponseDTO {
    private Long id;
    private String date;
    private String content;
}
