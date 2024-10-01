package foregg.foreggserver.dto.homeDTO;

import foregg.foreggserver.domain.enums.DailyConditionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResponseDTO {

    private String userName;
    private String spouseName;
    private String todayDate;
    private List<HomeRecordResponseDTO> homeRecordResponseDTO;
    private String ssn;
    private boolean todo;
    //남편에게만
    private DailyConditionType dailyConditionType;
    private String dailyContent;
    private String latestMedicalRecord;
    private Long medicalRecordId;

}
