package foregg.foreggserver.dto.myPageDTO;

import foregg.foreggserver.domain.enums.SurgeryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponseDTO {

    private Long id;
    private String nickname;
    private SurgeryType surgeryType;
    private int count;
    private String startDate;
    private String spouse;
    private String ssn;
    private String spouseCode;

}
