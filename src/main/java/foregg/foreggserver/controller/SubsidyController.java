package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
import foregg.foreggserver.dto.subsidyDTO.SubsidyResponseDTO;
import foregg.foreggserver.service.subsidyService.SubsidyQueryService;
import foregg.foreggserver.service.subsidyService.SubsidyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subsidy")
@PreAuthorize("isAuthenticated()")
@Tag(name = "지원금 API")
public class SubsidyController {

    private final SubsidyService subsidyService;
    private final SubsidyQueryService subsidyQueryService;

    @Operation(description = "지원금 생성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4003", description = "해당 회차의 같은 이름의 지원금이 존재합니다"),
    })
    @PostMapping("")
    public ApiResponse<String> createSubsidy(@RequestBody SubsidyRequestDTO dto) {
        subsidyService.createSubsidy(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(description = "지원금 수정 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4001", description = "나의 지원금이 존재하지 않습니다"),
    })
    @PutMapping("/{id}")
    public ApiResponse<String> updateSubsidy(@PathVariable(name = "id") Long id, @RequestBody SubsidyRequestDTO dto) {
        subsidyService.updateSubsidy(id, dto);
        return ApiResponse.onSuccess();
    }

    @Operation(description = "지원금 삭제 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4001", description = "나의 지원금이 존재하지 않습니다"),
    })
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSubsidy(@PathVariable(name = "id") Long id) {
        subsidyService.deleteSubsidy(id);
        return ApiResponse.onSuccess();
    }

    @Operation(description = "지원금 상세 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4001", description = "나의 지원금이 존재하지 않습니다"),
    })
    @GetMapping("/{id}")
    public ApiResponse<SubsidyRequestDTO> subsidyDetail(@PathVariable(name = "id") Long id) {
        SubsidyRequestDTO result = subsidyQueryService.detailSubsidy(id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(description = "회차별 지원금 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @GetMapping("/byCount/{count}")
    public ApiResponse<SubsidyResponseDTO> getSubsidyByCount(@PathVariable(name = "count") int count) {
        SubsidyResponseDTO result = subsidyQueryService.getSubsidyByCount(count);
        return ApiResponse.onSuccess(result);
    }

}
