package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.domain.enums.NotificationType;
import foregg.foreggserver.domain.enums.ChallengeSuccessDayType;
import foregg.foreggserver.dto.challengeDTO.ChallengeRequestDTO.ChallengeCompleteRequestDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeRequestDTO.ChallengeCreateRequestDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeRequestDTO.ChallengeNameRequestDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeParticipantDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.ChallengeParticipantsDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO;
import foregg.foreggserver.dto.challengeDTO.ChallengeResponseDTO.MyChallengeTotalDTO.MyChallengeDTO;
import foregg.foreggserver.service.challengeService.ChallengeQueryService;
import foregg.foreggserver.service.challengeService.ChallengeService;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserQueryService userQueryService;

    @Operation(summary = "챌린지 메인 API")
    @GetMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4011", description = "챌린지 닉네임을 먼저 만들어주세요"),
    })
    public ApiResponse<ChallengeResponseDTO> challengeMain() {
        ChallengeResponseDTO result = challengeQueryService.challengeMain();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "모든 챌린지 보기 API")
    @GetMapping("/all")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ChallengeResponseDTO> seeAllChallenges() {
        ChallengeResponseDTO result = challengeQueryService.getAllChallenges();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "참여하고 있는 챌린지 보기 API")
    @GetMapping("/my")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4002", description = "나의 챌린지가 존재하지 않습니다"),
    })
    public ApiResponse<MyChallengeTotalDTO> myChallenge() {
        MyChallengeTotalDTO result = challengeQueryService.getMyChallenges();
        return ApiResponse.onSuccess(result);
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
    @PatchMapping("/complete/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4003", description = "참여하고 있는 챌린지가 아닙니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4005", description = "이미 성공한 날짜입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4012", description = "오늘, 어제 날짜 이외에는 챌린지 성공 할 수 없습니다"),
    })
    public ApiResponse<String> complete(@PathVariable(name = "id") Long challengeId,
                                        @RequestParam(name = "date") String date,
                                        @RequestBody(required = false) ChallengeCompleteRequestDTO dto) {
        challengeService.success(challengeId, date, dto);
        return ApiResponse.onSuccess("성공입니다");
    }

    @Operation(summary = "해당 챌린지 수행 완료 취소 API")
    @DeleteMapping("/deleteTodayComplete/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4003", description = "참여하고 있는 챌린지가 아닙니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4006", description = "성공한 날짜가 없습니다"),
    })
    public ApiResponse<String> deleteTodayComplete(@PathVariable(name = "id") Long challengeId) {
        challengeService.deleteTodaySuccess(challengeId);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 닉네임 만들기, 2000 포인트 제공 API //이미 챌린지에 참여하고 있으면 301을 반환")
    @PatchMapping("/nickname")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REDIRECT", description = "리다이렉트 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4007", description = "챌린지 닉네임이 이미 존재합니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4008", description = "챌린지 닉네임이 중복됩니다"),
    })
    public ApiResponse<String> createChallengeName(@RequestBody ChallengeNameRequestDTO dto) {
        String status = challengeService.createChallengeName(dto);
        if (status.equals("301")) {
            return ApiResponse.redirect("챌린지에 참여 중인 회원입니다");
        }
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 잠금 해제")
    @PatchMapping("/unlock/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4010", description = "이미 오픈된 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POINT4001", description = "포인트가 부족합니다"),
    })
    public ApiResponse<String> openChallenge(@PathVariable(name = "id") Long id) {
        challengeService.unlock(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 참여하기")
    @PatchMapping("/participate/{id}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4001", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4004", description = "이미 참여하고 있는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4009", description = "오픈되지 않은 챌린지입니다"),
    })
    public ApiResponse<String> participateChallenge(@PathVariable(name = "id") Long id) {
        challengeService.participate(id);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 제작하기")
    @PostMapping("/create")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POINT4001", description = "포인트가 부족합니다"),
    })
    public ApiResponse<String> createChallenge(@RequestBody ChallengeCreateRequestDTO dto) {
        challengeService.createChallenge(dto);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 검색하기")
    @GetMapping("/search")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ChallengeResponseDTO> searchChallenge(@RequestParam(name = "keyword") String keyword) {
        ChallengeResponseDTO result = challengeQueryService.searchChallenge(keyword);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "챌린지 닉네임 조회하기")
    @GetMapping("/getChallengeName")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<String> getChallengeName() {
        return ApiResponse.onSuccess(userQueryService.getChallengeName());
    }

    @Operation(summary = "챌린지 찌르기 리스트")
    @GetMapping("/{challengeId}/{isSuccess}/participants")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PAGE4001", description = "페이지 사이즈를 벗어났습니다")
    })
    public ApiResponse<ChallengeParticipantsDTO> getCompleteList(
            @PathVariable(name = "challengeId") Long challengeId,
            @PathVariable(name = "isSuccess") boolean isSuccess,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ChallengeParticipantsDTO result = challengeQueryService.getParticipants(challengeId, isSuccess, pageable);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "챌린지 찌르기")
    @PostMapping("{challengeId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4002", description = "존재하지 않는 챌린지입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4003", description = "참여하고 있는 챌린지가 아닙니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4013", description = "응원과 박수는 각각 하루에 세 번까지 가능합니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4014", description = "이미 오늘 찌르기를 한 유저입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4015", description = "성공하지 않은 유저에게는 박수를 보낼 수 없습니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4016", description = "성공한 유저에게는 응원을 보낼 수 없습니다"),
    })
    public ApiResponse<String> cheer(@PathVariable(name = "challengeId") Long challengeId,
                                     @RequestParam(name = "cheerType") NotificationType cheerType,
                                     @RequestParam(name = "receiverId") Long receiverId) {
        challengeService.cheer(receiverId, cheerType, challengeId);
        return ApiResponse.onSuccess();
    }

    @Operation(summary = "챌린지 상세")
    @GetMapping("/{challengeId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHALLENGE4002", description = "존재하지 않는 챌린지입니다"),
    })
    public ApiResponse<ChallengeDTO> detail(@PathVariable(name = "challengeId") Long challengeId) {
        ChallengeDTO result = challengeQueryService.detail(challengeId);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "챌린지 찌르기 객체")
    @GetMapping("/{challengeId}/{userId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ChallengeParticipantDTO> challengeCheerObject(@PathVariable(name = "challengeId") Long challengeId,
                                                                     @PathVariable(name = "userId") Long userId) {
        ChallengeParticipantDTO result = challengeQueryService.challengeSupportDetail(challengeId, userId);
        return ApiResponse.onSuccess(result);
    }

}
