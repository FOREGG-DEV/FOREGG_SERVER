package foregg.foreggserver.dto.subsidyDTO;

import foregg.foreggserver.domain.enums.SubsidyColorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubsidyResponseDTO {

    private String period;
    private List<SubsidyDetailResponseDTO> subsidyDetailResponseDTOS;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubsidyDetailResponseDTO {
        private Long id;
        private String nickname;
        private SubsidyColorType color;
        private int amount;
        private int expenditure;
        private int available;
        private int percent;
    }
}
