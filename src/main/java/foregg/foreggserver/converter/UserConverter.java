package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;
import foregg.foreggserver.util.SpouseCodeGenerator;

public class UserConverter {

    public static User toUser(KakaoUserInfoResponse userInfo, String keyCode, Surgery surgery, UserJoinRequestDTO dto){
        return User.builder()
                .id(userInfo.getId())
                .keyCode(keyCode)
                .email("동의 후 삽입")
                .nickname(userInfo.getProperties().getNickname())
                .ssn(dto.getSsn())
                .surgery(surgery)
                .spouseCode(dto.getSpouseCode()).build();
    }

    public static UserResponseDTO toUserResponseDTO(String keycode, String jwt, String spouseCode) {
        return UserResponseDTO.builder()
                .keycode(keycode)
                .accessToken(jwt)
                .spouseCode(spouseCode)
                .build();
    }
}
