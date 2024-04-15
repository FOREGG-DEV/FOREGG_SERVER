package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.UserJoinRequestDTO;

public class SurgeryConverter {

    public static Surgery toSurgery(UserJoinRequestDTO surgeryInfo, User user) {
        return Surgery.builder()
                .surgeryType(surgeryInfo.getSurgeryType())
                .startAt(surgeryInfo.getStartAt())
                .count(surgeryInfo.getCount())
                .user(user).build();
    }
}
