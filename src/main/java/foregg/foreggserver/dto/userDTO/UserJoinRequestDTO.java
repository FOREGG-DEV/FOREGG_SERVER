package foregg.foreggserver.dto.userDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.enums.SurgeryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequestDTO {

    private SurgeryType surgeryType;
    private int count;
    private String startAt;

}
