package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.dailyDTO.*;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO.DailyAllResponseDTO;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO.DailyByCountResponseDTO;
import foregg.foreggserver.dto.injectionDTO.InjectionResponseDTO;
import foregg.foreggserver.service.dailyService.DailyQueryService;
import foregg.foreggserver.service.dailyService.DailyService;
import foregg.foreggserver.service.injectionService.InjectionQueryService;
import foregg.foreggserver.service.s3Service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
@Tag(name = "하루기록 API")
public class DailyController {

    private final DailyService dailyService;
    private final DailyQueryService dailyQueryService;
    private final InjectionQueryService injectionQueryService;
    private final S3Service s3Service;

    @Operation(summary = "전체 하루기록 보기 API")
    @GetMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<DailyAllResponseDTO> getAllDaily(@RequestParam(name = "page") int page) {
        DailyAllResponseDTO allDaily = dailyQueryService.getAllDaily(page);
        return ApiResponse.onSuccess(allDaily);
    }

    @Operation(summary = "하루기록 보기 API")
    @GetMapping("/byDate/{date}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("isAuthenticated()")

    public ApiResponse<DailyResponseDTO> getDaily(@PathVariable(name = "date") String date) {
        return ApiResponse.onSuccess(dailyQueryService.getDaily(date));
    }

    @Operation(summary = "회차별 하루기록 API")
    @GetMapping("/byCount/{count}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<List<DailyByCountResponseDTO>> dailyByCount(@PathVariable(name = "count") int count) {
        List<DailyByCountResponseDTO> result = dailyQueryService.dailyByCount(count);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "회차 전체 삭제 API")
    @DeleteMapping("/byCount/{count}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> deleteByCount(@PathVariable(name = "count") int count) {
        dailyService.deleteByCount(count);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "하루기록 작성 API")
    @PostMapping("/write")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4001", description = "오늘의 하루기록이 이미 존재합니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> write(@RequestPart(name = "image", required = false) MultipartFile image,
                                     @RequestPart(name = "dto") DailyRequestDTO dto) throws IOException {

        dailyService.writeDaily(dto, s3Service.upload(image));
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "하루기록 댓글 작성")
    @PostMapping("/reply")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4001", description = "오늘의 하루기록이 이미 존재합니다"),
    })
    @PreAuthorize("hasRole('ROLE_HUSBAND')")
    public ApiResponse<String> reply(@RequestBody DailyRequestDTO.DailyReplyRequestDTO dto) {
        dailyService.reply(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "하루기록 삭제 API")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4002", description = "하루기록이 존재하지 않습니다"),
    })
    public ApiResponse<String> delete(@PathVariable(name = "id") Long id) {
        dailyService.deleteDaily(id);
        return ApiResponse.onSuccess();
    }


    @Operation(summary = "하루기록 수정 API")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DAILY4002", description = "하루기록이 존재하지 않습니다"),
    })
    public ApiResponse<String> modify(@PathVariable(name = "id") Long id,
                                      @RequestPart(name = "image", required = false) MultipartFile image,
                                      @RequestPart(name = "dto") DailyRequestDTO dto) throws IOException {
        dailyService.modifyDaily(id, dto, image);
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

    @Operation(summary = "부작용 수정 API")
    @PutMapping("/sideEffect/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SIDEEFFECT4001", description = "부작용이 존재하지 않습니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> modifySideEffect(@PathVariable(name = "id") Long id, @RequestBody SideEffectRequestDTO dto) {
        dailyService.modifySideEffect(id, dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "부작용 삭제 API")
    @DeleteMapping("/sideEffect/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SIDEEFFECT4001", description = "부작용이 존재하지 않습니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> deleteSideEffect(@PathVariable(name = "id") Long id) {
        dailyService.deleteSideEffect(id);
        return ApiResponse.onSuccess();
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
                                                              @RequestParam(name = "time") String time) {
        InjectionResponseDTO dto = injectionQueryService.getInjectionInfo(id, time);
        return ApiResponse.onSuccess(dto);
    }
}
