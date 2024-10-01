package foregg.foreggserver.dto.myPageDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageRecordResponseDTO {

    private Long id;
    private String date;
    private String startDate;
    private String endDate;
    private String repeatDays;
    private String name;
    private String dose;

}
