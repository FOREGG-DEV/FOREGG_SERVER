package foregg.foreggserver.dto.myPageDTO;

import foregg.foreggserver.domain.enums.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageBoardResponseDTO {

    private Long id;
    private BoardType boardType;
    private String title;
    private String content;
    private String date;

}
