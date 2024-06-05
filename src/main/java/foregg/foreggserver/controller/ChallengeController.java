package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.dto.challengeDTO.ChallengeAllResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeMyResponseDTO;
import foregg.foreggserver.service.challengeService.ChallengeQueryService;
import foregg.foreggserver.service.challengeService.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
@Tag(name = "챌린지 API")
@PreAuthorize("hasRole('ROLE_WIFE')")
public class ChallengeController {

    private final ChallengeQueryService challengeQueryService;
    private final ChallengeService challengeService;

    @Operation(summary = "모든 챌린지 보기 API")
    @GetMapping("/all")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<List<ChallengeAllResponseDTO>> seeAllChallenges() {
        List<ChallengeAllResponseDTO> result = challengeQueryService.getAllChallenges();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "챌린지 참가하기 API")
    @PostMapping("/participation/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4004", description = "이미 참여하고 있는 챌린지입니다"),
    })
    public ApiResponse<String> participate(@PathVariable(name = "id") Long id) {
        challengeService.participate(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "참여하고 있는 챌린지 보기 API")
    @GetMapping("/my")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4002", description = "나의 챌린지가 존재하지 않습니다"),
    })
    public ApiResponse<List<ChallengeMyResponseDTO>> seeMyChallenges() {
        List<ChallengeMyResponseDTO> myChallenges = challengeQueryService.getMyChallenges();
        return ApiResponse.onSuccess(myChallenges);
    }

    @Operation(summary = "챌린지 그만두기 API")
    @DeleteMapping("/quit/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4003", description = "참여하고 있는 챌린지가 아닙니다"),
    })
    public ApiResponse<String> quitChallenge(@PathVariable(name = "id") Long challengeId) {
        challengeService.quitChallenge(challengeId);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "해당 챌린지 수행 완료 API")
    @PostMapping("/complete/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4003", description = "참여하고 있는 챌린지가 아닙니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4005", description = "이미 성공한 날짜입니다"),
    })
    public ApiResponse<String> complete(@PathVariable(name = "id") Long challengeId) {
        challengeService.success(challengeId);
        return ApiResponse.onSuccess();
    }

    @DeleteMapping("/deleteTodayComplete/{id}")
    public ApiResponse<String> deleteTodayComplete(@PathVariable(name = "id") Long challengeId) {
        challengeService.deleteTodaySuccess(challengeId);
        return ApiResponse.onSuccess();
    }

}
