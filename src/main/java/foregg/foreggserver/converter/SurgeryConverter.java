package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.domain.enums.SurgeryType;
import foregg.foreggserver.dto.UserJoinRequestDTO;

public class SurgeryConverter {

    public static Surgery toSurgery(UserJoinRequestDTO surgeryInfo) {
        return Surgery.builder()
                .surgeryType(surgeryInfo.getSurgeryType())
                .startAt(surgeryInfo.getStartAt())
                .count(surgeryInfo.getCount()).build();
    }
}
