package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Subsidy;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.SubsidyColorType;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
import foregg.foreggserver.dto.subsidyDTO.SubsidyResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class SubsidyConverter {

    public static Subsidy toSubsidy(SubsidyRequestDTO dto, User user, SubsidyColorType color) {
        return Subsidy.builder()
                .count(dto.getCount())
                .content(dto.getContent())
                .amount(dto.getAmount())
                .available(dto.getAmount())
                .expenditure(0)
                .nickname(dto.getNickname())
                .color(color)
                .user(user).build();
    }

    public static SubsidyResponseDTO toDetailResponseDTO(String period, List<Subsidy> subsidies) {
        List<SubsidyResponseDTO.SubsidyDetailResponseDTO> detailResponseDTOS = new ArrayList<>();
        for (Subsidy ss : subsidies) {
            SubsidyResponseDTO.SubsidyDetailResponseDTO result = SubsidyResponseDTO.SubsidyDetailResponseDTO.builder()
                    .id(ss.getId())
                    .nickname(ss.getNickname())
                    .amount(ss.getAmount())
                    .expenditure(ss.getExpenditure())
                    .available(ss.getAvailable())
                    .percent(ss.getExpenditure() == 0 ? 0 : (int)(((double) ss.getExpenditure() / ss.getAmount()) * 100))
                    .build();
            detailResponseDTOS.add(result);
        }
        return SubsidyResponseDTO.builder()
                .period(period)
                .subsidyDetailResponseDTOS(detailResponseDTOS)
                .build();
    }

}
