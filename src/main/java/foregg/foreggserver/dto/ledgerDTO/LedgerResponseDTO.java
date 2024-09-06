package foregg.foreggserver.dto.ledgerDTO;

import foregg.foreggserver.domain.enums.SubsidyColorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerResponseDTO {

    private int personalSum;
    //월별,전체에 한함
    private Integer subsidySum;
    //회차별에 한함
    private List<SubsidyAvailable> subsidyAvailable;
    private int total;
    private List<LedgerDetailResponseDTO> ledgerDetailResponseDTOS;


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LedgerDetailResponseDTO {
        private Long id;
        private String date;
        private int count;
        private SubsidyColorType color;
        private String name;
        private String content;
        private int amount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubsidyAvailable{
        private String nickname;
        private int amount;
    }

}
