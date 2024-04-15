package foregg.foreggserver.converter;

import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.util.SpouseCodeGenerator;

public class UserConverter {

    public static User toUser(KakaoUserInfoResponse userInfo, String keyCode){
        return User.builder()
                .id(userInfo.getId())
                .keyCode(keyCode)
                .email("동의 후 삽입")
                .name("동의 후 삽입")
                .nickname(userInfo.getProperties().getNickname())
                .gender("동의 후 삽입")
                .birthDate("동의 후 삽입")
                .yearOfBirth("동의 후 삽입")
                .spouseCode(SpouseCodeGenerator.generateRandomCode()).build();
    }
}