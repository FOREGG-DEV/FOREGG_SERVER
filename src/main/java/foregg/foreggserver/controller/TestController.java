package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.domain.Emoji;
import foregg.foreggserver.repository.EmojiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/test")
@RestController
public class TestController {

    private final EmojiRepository emojiRepository;

    @Operation(summary = "이모지 100개 테스트 API")
    @GetMapping("/emoji")
    public ApiResponse<List<Emoji>> emojiTest() {
        List<Emoji> result = emojiRepository.findAll();
        return ApiResponse.onSuccess(result);
    }

}
