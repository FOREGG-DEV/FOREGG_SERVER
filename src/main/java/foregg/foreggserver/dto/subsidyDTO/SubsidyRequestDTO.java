package foregg.foreggserver.dto.subsidyDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SubsidyRequestDTO {

    private String nickname;
    private String content;
    private int count;
    private int amount;
}
