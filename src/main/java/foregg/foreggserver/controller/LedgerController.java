package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.service.ledgerService.LedgerQueryService;
import foregg.foreggserver.service.ledgerService.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ledger")
@PreAuthorize("isAuthenticated()")
@Tag(name = "가계부 API")
public class LedgerController {

    private final LedgerService ledgerService;
    private final LedgerQueryService ledgerQueryService;

    @Operation(summary = "가계부 추가 API")
    @PostMapping("/add")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4001", description = "나의 지원금이 존재하지 않습니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBSIDY4002", description = "지원금의 한도가 초과되었습니다"),
    })
    public ApiResponse<String> writeLedger(@RequestBody LedgerRequestDTO dto) {
        ledgerService.writeLedger(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "가계부 삭제 API")
    @DeleteMapping("/{id}/delete")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<String> deleteLedger(@PathVariable(name = "id") Long id) {
        ledgerService.deleteLedger(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "가계부 수정 API")
    @PutMapping("/{id}/modify")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<String> modifyLedger(@PathVariable(name = "id") Long id, @RequestBody LedgerRequestDTO dto) {
        ledgerService.modifyLedger(dto, id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "가계부 상세 API")
    @GetMapping("/{id}/detail")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<LedgerRequestDTO> detail(@PathVariable(name = "id") Long id) {
        LedgerRequestDTO result = ledgerQueryService.ledgerDetail(id);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "전체(30일) 가계부 보기 API")
    @GetMapping("/all")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<LedgerResponseDTO> all() {
        LedgerResponseDTO totalResponse = ledgerQueryService.all();
        return ApiResponse.onSuccess(totalResponse);
    }

    @Operation(summary = "월별 가계부 API")
    @GetMapping("/byMonth")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<LedgerResponseDTO> byCount(@RequestParam(name = "yearmonth") String yearmonth) {
        LedgerResponseDTO result = ledgerQueryService.byMonth(yearmonth);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "회차별 가계부 INIT API")
    @GetMapping("/byCountInit")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SURGERY4001", description = "나의 시술이 존재하지 않습니다"),
    })
    public ApiResponse<LedgerResponseDTO> byCount() {
        LedgerResponseDTO result = ledgerQueryService.byCountInit();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "회차별 가계부 API")
    @GetMapping("/byCount")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<LedgerResponseDTO> byCount(@RequestParam(name = "count") int count) {
        LedgerResponseDTO result = ledgerQueryService.byCount(count);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "새 회차 추가 API")
    @PostMapping("/createCount")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SURGERY4001", description = "나의 시술이 존재하지 않습니다"),
    })
    public ApiResponse<String> createCount() {
        ledgerService.createCount();
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "날짜 조건 검색 가계부 API")
    @GetMapping("/byCondition")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<LedgerResponseDTO> byCondition(@RequestParam(name = "from") String from,
                                                      @RequestParam(name = "to") String to) {
        LedgerResponseDTO result = ledgerQueryService.byCondition(from, to);
        return ApiResponse.onSuccess(result);
    }

    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EXPENDITURE4001", description = "해당 지출이 존재하지 않습니다"),
    })
    @DeleteMapping("/expenditure/{id}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> deleteExpenditure(@PathVariable(name = "id") Long id) {
        ledgerService.deleteExpenditure(id);
        return ApiResponse.onSuccess();
    }

    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "해당 회차의 가계부가 존재하지 않습니다"),
    })
    @PutMapping("/memo/{count}")
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> memo(@PathVariable(name = "count") int count, @RequestBody LedgerMemoRequestDTO dto) {
        ledgerService.memo(count,dto);
        return ApiResponse.onSuccess();
    }


}
