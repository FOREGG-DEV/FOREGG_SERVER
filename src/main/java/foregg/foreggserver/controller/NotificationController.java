package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.notificationDTO.NotificationResponseDTO;
import foregg.foreggserver.service.notificationService.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @Operation(summary = "알림 히스토리 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @GetMapping("/history")
    public ApiResponse<List<NotificationResponseDTO>> getNotificationHistory() {
        List<NotificationResponseDTO> result = notificationQueryService.getNotificationHistory();
        return ApiResponse.onSuccess(result);
    }

}
