package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.ReplyEmojiType;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO.DailyAllResponseDTO;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO.DailyByCountResponseDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectResponseDTO;
import foregg.foreggserver.util.DateUtil;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DailyConverter {

    public static DailyAllResponseDTO toDailyAllResponse(Page<Daily> dailyPage) {
        List<DailyByCountResponseDTO> result = new ArrayList<>();
        for (Daily daily : dailyPage.getContent()) {
            DailyByCountResponseDTO dto = DailyByCountResponseDTO.builder()
                    .id(daily.getId())
                    .dailyConditionType(daily.getDailyConditionType())
                    .date(daily.getDate())
                    .content(daily.getContent())
                    .build();
            result.add(dto);
        }

        return DailyAllResponseDTO.builder()
                .dto(result)
                .currentPage(dailyPage.getNumber())
                .totalPages(dailyPage.getTotalPages())
                .totalItems(dailyPage.getTotalElements())
                .build();
    }


    public static Daily toDaily(DailyRequestDTO dto, User user, String imageUrl, int count) {
        return Daily.builder()
                .dailyConditionType(dto.getDailyConditionType())
                .content(dto.getContent())
                .count(count)
                .date(LocalDate.now().toString())
                .image(imageUrl)
                .user(user).build();
    }

    public static DailyResponseDTO toDailyResponseDTO(Daily daily, String specialQuestion) {
        String replyContent = null;
        ReplyEmojiType replyEmojiType = null;
        if (daily.getReply() != null) {
            replyContent = daily.getReply().getContent();
            replyEmojiType = daily.getReply().getReplyEmojiType();
        }

        return DailyResponseDTO.builder()
                .id(daily.getId())
                .count(daily.getCount())
                .date(daily.getDate())
                .day(DateUtil.getDayOfWeekFromString(daily.getDate()))
                .dailyConditionType(daily.getDailyConditionType())
                .content(daily.getContent())
                .imageUrl(daily.getImage())
                .replyContent(replyContent)
                .replyEmojiType(replyEmojiType)
                .specialQuestion(specialQuestion)
                .build();
    }

    public static List<DailyByCountResponseDTO> toDailyByCountResponseDTO(List<Daily> dailies) {
        List<DailyByCountResponseDTO> result = new ArrayList<>();
        for (Daily daily : dailies) {
            DailyByCountResponseDTO dto = DailyByCountResponseDTO.builder()
                    .id(daily.getId())
                    .dailyConditionType(daily.getDailyConditionType())
                    .content(daily.getContent())
                    .date(DateUtil.convertToMonthDay(daily.getDate()))
                    .build();
            result.add(dto);
        }
        return result;
    }

    public static SideEffect toSideEffect(SideEffectRequestDTO dto, Record record, User user) {
        return SideEffect.builder()
                .content(dto.getContent())
                .date(DateUtil.formatLocalDateTime(LocalDate.now()))
                .record(record)
                .user(user)
                .build();
    }

    public static List<SideEffectResponseDTO> toSideEffectResponseDTO(List<SideEffect> sideEffects) {
        List<SideEffectResponseDTO> resultList = new ArrayList<>();
        if (sideEffects == null) {
            return null;
        }
        for (SideEffect sideEffect : sideEffects) {
            resultList.add(SideEffectResponseDTO.builder().id(sideEffect.getId()).date(sideEffect.getDate())
                    .content(sideEffect.getContent()).build());
        }
        return resultList;
    }

}
