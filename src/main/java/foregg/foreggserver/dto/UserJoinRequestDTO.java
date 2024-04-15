package foregg.foreggserver.dto;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.enums.SurgeryType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class UserJoinRequestDTO {

    private SurgeryType surgeryType;
    private int count;
    private LocalDate startAt;

}
