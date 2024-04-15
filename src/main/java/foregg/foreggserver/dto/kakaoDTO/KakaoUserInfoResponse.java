package foregg.foreggserver.dto.kakaoDTO;

import lombok.Getter;

@Getter
public class KakaoUserInfoResponse {

    //회원가입 상태인지 아닌지를 판단하는 필드
    private Long id;
    private String connected_at;
    private KakaoProperties properties;
    private KakaoAccount kakao_account;
}
