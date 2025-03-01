package foregg.foreggserver.dto.dailyDTO;

import foregg.foreggserver.domain.enums.DailyConditionType;
import foregg.foreggserver.domain.enums.ReplyEmojiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyResponseDTO {

    private Long id;
    private int count;
    private String day;
    private DailyConditionType dailyConditionType;
    private String content;
    private String imageUrl;
    private String replyContent;
    private ReplyEmojiType replyEmojiType;
    private String specialQuestion;
    private String createdAt;
    private String modifiedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyAllResponseDTO {
        private List<DailyByCountResponseDTO> dto;
        private int currentPage;
        private int totalPages;
        private long totalItems;
    }

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
