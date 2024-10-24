package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.dto.homeDTO.CheckResponseDTO;
import foregg.foreggserver.dto.recordDTO.MedicalRecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.MedicalRecordResponseDTO;
import foregg.foreggserver.dto.recordDTO.RecordRequestDTO;
import foregg.foreggserver.dto.recordDTO.RecordResponseDTO;
import foregg.foreggserver.service.recordService.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
@Tag(name = "일정 CRUD API")
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "일정 추가 API")
    @PostMapping("/add")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4010", description = "남편은 기타 일정 외에는 추가할 수 없습니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecordResponseDTO> addRecord(@RequestBody RecordRequestDTO dto) {
        RecordResponseDTO resultDTO = recordService.addRecord(dto);
        return ApiResponse.onSuccess(resultDTO);
    }

    @Operation(summary = "일정 삭제 API")
    @Parameters({
            @Parameter(name = "id", description = "일정의 ID 입니다")
    })
    @DeleteMapping("/{id}/delete")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> deleteRecord(@PathVariable("id") Long id) {
        recordService.deleteRecord(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "일정 수정 API")
    @Parameters({
            @Parameter(name = "id", description = "일정의 ID 입니다")
    })
    @PutMapping("/{id}/modify")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> modifyRecord(@PathVariable("id") Long id, @RequestBody RecordRequestDTO dto) {
        recordService.modifyRecord(id, dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "일정 상세 API")
    @Parameters({
            @Parameter(name = "id", description = "일정의 ID 입니다")
    })
    @GetMapping("/{id}/detail")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<RecordResponseDTO> specificRecord(@PathVariable(name = "id") Long id) {
        RecordResponseDTO resultDTO = recordService.recordDetail(id);
        return ApiResponse.onSuccess(resultDTO);
    }

    @Operation(summary = "일정에 진료기록 추가 API")
    @Parameters(value = {
            @Parameter(name = "id", description = "일정의 ID 입니다"),
    })
    @PostMapping("/{id}/medicalRecord")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4002", description = "병원 일정이 아닙니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> addMedicalRecord(@PathVariable(name = "id") Long id, @RequestBody MedicalRecordRequestDTO dto) {
        recordService.addMedicalRecord(id, dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "일정 진료기록 및 부작용 보기")
    @Parameters(value = {
            @Parameter(name = "id", description = "일정의 ID 입니다"),
    })
    @GetMapping("/{id}/medicalRecordAndSideEffect")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4002", description = "병원 일정이 아닙니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MedicalRecordResponseDTO> medicalRecordAndSideEffect(@PathVariable(name = "id") Long id) {
        MedicalRecordResponseDTO dto = recordService.medicalRecordAndSideEffect(id);
        return ApiResponse.onSuccess(dto);
    }

    @PatchMapping("/checkTodo/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4001", description = "존재하지 않는 일정입니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<CheckResponseDTO> checkTodo(@PathVariable(name = "id") Long id, @RequestParam(name = "time") String time) {
        CheckResponseDTO dto = recordService.checkTodo(id, time);
        return ApiResponse.onSuccess(dto);
    }

}
