package foregg.foreggserver.service.userService;

import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoRequestService { // 카카오 API를 이용해 토큰을 전송하여 유저 정보를 요청

    private final WebClient webClient;
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoResponse getUserInfo(String token) {
        Flux<KakaoUserInfoResponse> response = webClient.get()
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(KakaoUserInfoResponse.class);
        return response.blockFirst();
    }
}