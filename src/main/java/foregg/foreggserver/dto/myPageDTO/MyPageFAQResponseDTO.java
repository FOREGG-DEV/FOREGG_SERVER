package foregg.foreggserver.dto.myPageDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageFAQResponseDTO {

    private Long id;
    private String question;
    private String answer;

}
