package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.dailyDTO.*;
import foregg.foreggserver.dto.injectionDTO.InjectionResponseDTO;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.dailyService.DailyService;
import foregg.foreggserver.service.injectionService.InjectionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
@Tag(name = "하루기록 API")
public class DailyController {

    private final DailyService dailyService;
    private final DailyQueryService dailyQueryService;
    private final InjectionQueryService injectionQueryService;

    @Operation(summary = "하루기록 보기 API")
    @GetMapping()
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DailyTotalResponseDTO> getDaily() {
        return ApiResponse.onSuccess(dailyQueryService.getDaily());
    }

    @Operation(summary = "하루기록 작성 API")
    @PostMapping("/write")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4001", description = "오늘의 하루기록이 이미 존재합니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
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
    @PreAuthorize("hasRole('ROLE_HUSBAND')")
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
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> sideEffect(@RequestBody SideEffectRequestDTO dto) {
        dailyService.writeSideEffect(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "부작용 보기 API")
    @GetMapping("/sideEffectList")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<List<SideEffectResponseDTO>> writeSideEffect() {
        List<SideEffectResponseDTO> sideEffectList = dailyService.getSideEffectList();
        return ApiResponse.onSuccess(sideEffectList);
    }

    @Operation(summary = "주사 투여 완료 공유하기 API")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @PostMapping("/shareInjection/{id}")
    public ApiResponse<String> sendNotificationInjection(@PathVariable(name = "id") Long id,
                                                         @RequestParam(name = "time") String time) {
        injectionQueryService.shareInjection(id, time);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "주사 정보 페이지 API")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/injectionInfo/{id}")
    public ApiResponse<InjectionResponseDTO> getInjectionInfo(@PathVariable(name = "id") Long id,
                                                              @RequestParam(name = "time")String time) {
        InjectionResponseDTO dto = injectionQueryService.getInjectionInfo(id, time);
        return ApiResponse.onSuccess(dto);
    }
}
