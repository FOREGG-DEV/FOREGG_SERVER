package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectResponseDTO;
import foregg.foreggserver.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyConverter {

    public static Daily toDaily(DailyRequestDTO dto, User user) {
        return Daily.builder()
                .dailyConditionType(dto.getDailyConditionType())
                .content(dto.getContent())
                .date(DateUtil.formatLocalDateTime(LocalDate.now()))
                .emotionType(null)
                .user(user).build();
    }

    public static DailyResponseDTO toDailyResponseDTO(Daily daily) {
        return DailyResponseDTO.builder()
                .id(daily.getId())
                .dailyConditionType(daily.getDailyConditionType())
                .content(daily.getContent())
                .date(daily.getDate())
                .emotionType(daily.getEmotionType())
                .build();
    }

    public static SideEffect toSideEffect(SideEffectRequestDTO dto, Record record) {
        return SideEffect.builder()
                .content(dto.getContent())
                .date(DateUtil.formatLocalDateTime(LocalDate.now()))
                .record(record)
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
