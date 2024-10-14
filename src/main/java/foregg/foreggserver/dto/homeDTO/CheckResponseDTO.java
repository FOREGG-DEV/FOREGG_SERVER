package foregg.foreggserver.dto.homeDTO;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckResponseDTO {

    CheckInSameRecordResponseDTO dto;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckInSameRecordResponseDTO {
        private Long recordId;
        private List<String> times;
    }
}

