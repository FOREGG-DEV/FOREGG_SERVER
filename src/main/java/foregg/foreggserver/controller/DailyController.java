package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import foregg.foreggserver.dto.dailyDTO.DailyTotalResponseDTO;
import foregg.foreggserver.dto.dailyDTO.EmotionRequestDTO;
import foregg.foreggserver.dto.dailyDTO.SideEffectRequestDTO;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.dailyService.DailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyController {

    private final DailyService dailyService;
    private final DailyQueryService dailyQueryService;

    @GetMapping()
    public ApiResponse<DailyTotalResponseDTO> getDaily() {
        return ApiResponse.onSuccess(dailyQueryService.getDaily());
    }

    @PostMapping("/write")
    public ApiResponse<String> write(@RequestBody DailyRequestDTO dto) {
        dailyService.writeDaily(dto);
        return ApiResponse.onSuccess();
    }

    @PutMapping("/{id}/emotion")
    public ApiResponse<String> emotion(@PathVariable(name = "id") Long id, @RequestBody EmotionRequestDTO dto) {
        dailyService.putEmotion(id, dto);
        return ApiResponse.onSuccess();
    }

    @PostMapping("/sideEffect")
    public ApiResponse<String> sideEffect(@RequestBody SideEffectRequestDTO dto) {
        dailyService.writeSideEffect(dto);
        return ApiResponse.onSuccess();
    }

}
