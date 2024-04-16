package foregg.foreggserver.dto;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.enums.SurgeryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequestDTO {

    private SurgeryType surgeryType;
    private int count;
    private LocalDate startAt;

}
