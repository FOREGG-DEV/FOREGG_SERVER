package foregg.foreggserver.dto.dailyDTO;

import foregg.foreggserver.domain.enums.DailyConditionType;
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
    private int count;
    private String date;
    private String day;
    private DailyConditionType dailyConditionType;
    private String content;
    private String imageUrl;
    private String reply;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyByCountResponseDTO {
        private Long id;
        private String date;
        private DailyConditionType dailyConditionType;
        private String content;
    }

}
