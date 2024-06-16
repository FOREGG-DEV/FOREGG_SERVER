package foregg.foreggserver.dto.informationDTO;

import foregg.foreggserver.domain.Information;
import foregg.foreggserver.domain.enums.InformationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformationResponseDTO {

    private Long id;
    private InformationType informationType;
    private List<String> tag;
    private String image;
    private String url;

}
