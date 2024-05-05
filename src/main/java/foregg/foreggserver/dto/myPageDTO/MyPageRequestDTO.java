package foregg.foreggserver.dto.myPageDTO;

import foregg.foreggserver.domain.enums.SurgeryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageRequestDTO {

    private SurgeryType surgeryType;
    private int count;
    private String startDate;

}
