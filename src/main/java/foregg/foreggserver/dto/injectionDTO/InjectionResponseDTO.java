package foregg.foreggserver.dto.injectionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InjectionResponseDTO {
    private String name;
    private String description;
    private String image;
    private String time;
}
