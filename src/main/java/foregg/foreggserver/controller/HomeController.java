package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.homeDTO.HomeResponseDTO;
import foregg.foreggserver.service.recordService.RecordQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/home")
@RestController
@Tag(name = "홈화면 API")
public class HomeController {

    private final RecordQueryService recordQueryService;

    @Operation(summary = "홈(오늘의 일정, 기록 보기) API")
    @GetMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "존재하지 않는 사용자입니다."),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<HomeResponseDTO> home() {
        HomeResponseDTO todayRecord = recordQueryService.getTodayRecord();
        return ApiResponse.onSuccess(todayRecord);
    }

}
