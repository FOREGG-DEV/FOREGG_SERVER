package foregg.foreggserver.dto.recordDTO;

import foregg.foreggserver.domain.SideEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponseDTO {

    private String medicalRecord;
    private List<SideEffect> sideEffects;

}
