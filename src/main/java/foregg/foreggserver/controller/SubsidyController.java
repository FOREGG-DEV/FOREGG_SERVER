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
@Tag(name = "지원금 API")
public class SubsidyController {

    private final SubsidyService subsidyService;
    private final SubsidyQueryService subsidyQueryService;

    @Operation(description = "지원금 생성 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
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
    @PreAuthorize("hasRole('ROLE_WIFE')")
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
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSubsidy(@PathVariable(name = "id") Long id) {
        subsidyService.deleteSubsidy(id);
        return ApiResponse.onSuccess();
    }

    @Operation(description = "회차별 지원금 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @GetMapping("/byCount/{count}")
    public ApiResponse<SubsidyResponseDTO> getSubsidyByCount(@PathVariable(name = "count") int count) {
        SubsidyResponseDTO result = subsidyQueryService.getSubsidyByCount(count);
        return ApiResponse.onSuccess(result);
    }



}
