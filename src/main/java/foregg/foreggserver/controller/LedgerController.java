package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerResponseDTO;
import foregg.foreggserver.dto.ledgerDTO.LedgerTotalResponseDTO;
import foregg.foreggserver.service.ledgerService.LedgerQueryService;
import foregg.foreggserver.service.ledgerService.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ledger")
@Tag(name = "가계부 API")
@PreAuthorize("isAuthenticated()")
public class LedgerController {

    private final LedgerQueryService ledgerQueryService;
    private final LedgerService ledgerService;

    @Operation(summary = "전체(30일) 가계부 보기 API")
    @GetMapping("/all")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<LedgerTotalResponseDTO> all() {
        LedgerTotalResponseDTO totalResponse = ledgerQueryService.all();
        return ApiResponse.onSuccess(totalResponse);
    }

    @Operation(summary = "회차별 가계부 API")
    @GetMapping("/byCount")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<LedgerTotalResponseDTO> byCount(@RequestParam(name = "count") int count) {
        return ApiResponse.onSuccess(ledgerQueryService.byCount(count));
    }

    @Operation(summary = "월별 가계부 API")
    @GetMapping("/byMonth")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<LedgerTotalResponseDTO> byMonth(@RequestParam(name = "yearmonth") String yearmonth) {
        return ApiResponse.onSuccess(ledgerQueryService.byMonth(yearmonth));
    }

    @Operation(summary = "날짜 조건 검색 가계부 API")
    @GetMapping("/byCondition")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4002", description = "나의 가계부가 존재하지 않습니다"),
    })
    public ApiResponse<LedgerTotalResponseDTO> byCondition(@RequestParam(name = "from") String from,
                                                           @RequestParam(name = "to") String to) {
        return ApiResponse.onSuccess(ledgerQueryService.byCondition(from, to));
    }

    @Operation(summary = "가계부 추가 API")
    @PostMapping("/add")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> add(@RequestBody LedgerRequestDTO dto) {
        ledgerService.add(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "가계부 삭제 API")
    @DeleteMapping("/{id}/delete")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4001", description = "존재하지 않는 가계부입니다"),
    })
    public ApiResponse<String> delete(@PathVariable(name = "id") Long id) {
        ledgerService.delete(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "가계부 상세 API")
    @GetMapping("/{id}/detail")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4001", description = "존재하지 않는 가계부입니다"),
    })
    public ApiResponse<LedgerResponseDTO> detail(@PathVariable(name = "id") Long id) {
        LedgerResponseDTO detail = ledgerQueryService.detail(id);
        return ApiResponse.onSuccess(detail);
    }

    @Operation(summary = "가계부 수정 API")
    @PutMapping("/{id}/modify")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "LEDGER4001", description = "존재하지 않는 가계부입니다"),
    })
    public ApiResponse<String> modify(@PathVariable(name = "id") Long id, @RequestBody LedgerRequestDTO dto) {
        ledgerService.modify(id, dto);
        return ApiResponse.onSuccess();
    }

}
