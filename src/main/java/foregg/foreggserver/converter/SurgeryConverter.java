package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.dto.myPageDTO.MyPageRequestDTO;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;

public class SurgeryConverter {

    public static Surgery toSurgery(UserJoinRequestDTO surgeryInfo) {
        return Surgery.builder()
                .surgeryType(surgeryInfo.getSurgeryType())
                .startAt(surgeryInfo.getStartAt())
                .count(surgeryInfo.getCount()).build();
    }

    public static MyPageRequestDTO toMyPageRequestDTO(Surgery surgery) {
        return MyPageRequestDTO.builder()
                .surgeryType(surgery.getSurgeryType())
                .count(surgery.getCount())
                .startDate(surgery.getStartAt())
                .build();
    }
}
