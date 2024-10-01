package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.myPageDTO.*;
import foregg.foreggserver.service.myPageService.MyPageQueryService;
import foregg.foreggserver.service.myPageService.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
@Tag(name = "마이페이지 API")
public class MyPageController {

    private final MyPageService myPageService;
    private final MyPageQueryService myPageQueryService;

    @Operation(summary = "마이페이지 내 정보 API")
    @GetMapping()
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SURGERY4001", description = "나의 시술이 존재하지 않습니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MyPageResponseDTO> myPage() {
        return ApiResponse.onSuccess(myPageQueryService.getInformation());
    }

    @Operation(summary = "내 정보 수정 API")
    @PutMapping("/modifySurgery")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SURGERY4001", description = "나의 시술이 존재하지 않습니다"),
    })
    @PreAuthorize("hasRole('ROLE_WIFE')")
    public ApiResponse<String> modify(@RequestBody MyPageRequestDTO dto) {
        myPageService.modifySurgery(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "주사, 약 기록 API")
    @GetMapping("/medicalInfo")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "RECORD4004", description = "나의 기록이 존재하지 않습니다"),
    })
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MyPageMedicalRecordResponseDTO> medicalInfo(@RequestParam(name = "sort") String sort) {
        MyPageMedicalRecordResponseDTO result = myPageQueryService.getMedicalInformation(sort);
        return ApiResponse.onSuccess(result);
    }
}
