package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.recordDTO.ScheduleResponseDTO;
import foregg.foreggserver.service.recordService.RecordQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
@Tag(name = "캘린더 보기")
public class ScheduleController {

    private final RecordQueryService recordQueryService;

    @Operation(summary = "캘린더 보기")
    @Parameters(value = {
            @Parameter(name = "yearmonth", description = "캘린더 연월입니다. RequestParam입니다"),
    })
    @GetMapping()
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ScheduleResponseDTO> calendar(@RequestParam(name = "yearmonth") String yearmonth) {
        ScheduleResponseDTO calendar = recordQueryService.calendar(yearmonth);
        return ApiResponse.onSuccess(calendar);
    }
}
