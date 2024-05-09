package foregg.foreggserver.dto.myPageDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageMedicalRecordResponseDTO {

    private List<MyPageRecordResponseDTO> myPageRecordResponseDTO;

}
