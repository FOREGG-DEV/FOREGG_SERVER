package foregg.foreggserver.dto.injectionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalResponseDTO {
    private String name;
    private String date;
    private String description;
    private String image;
    private String time;
}
