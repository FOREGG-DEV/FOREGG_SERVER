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

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyReplyRequestDTO {
        private Long id;
        private String reply;
    }
}
