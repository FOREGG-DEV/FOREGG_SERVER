package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.DailyTotalResponseDTO;
import foregg.foreggserver.dto.dailyDTO.EmotionRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.dailyService.DailyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
@Tag(name = "하루기록 API")
public class DailyController {

    private final DailyService dailyService;
    private final DailyQueryService dailyQueryService;

    @Operation(summary = "하루기록 보기 API")
    @GetMapping()
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<DailyTotalResponseDTO> getDaily() {
        return ApiResponse.onSuccess(dailyQueryService.getDaily());
    }

    @Operation(summary = "하루기록 작성 API")
    @PostMapping("/write")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4001", description = "오늘의 하루기록이 이미 존재합니다"),
    })
    public ApiResponse<String> write(@RequestBody DailyRequestDTO dto) {
        dailyService.writeDaily(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "하루기록 이모지 API")
    @PutMapping("/{id}/emotion")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4002", description = "해당 하루기록이 존재하지 않습니다"),
    })
    public ApiResponse<String> emotion(@PathVariable(name = "id") Long id, @RequestBody EmotionRequestDTO dto) {
        dailyService.putEmotion(id, dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "부작용 작성 API")
    @PostMapping("/sideEffect")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4005", description = "예약된 병원 기록이 존재하지 않습니다"),
    })
    public ApiResponse<String> sideEffect(@RequestBody SideEffectRequestDTO dto) {
        dailyService.writeSideEffect(dto);
        return ApiResponse.onSuccess();
    }

}
