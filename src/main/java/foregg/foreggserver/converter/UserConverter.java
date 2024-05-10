package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Surgery;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.kakaoDTO.KakaoUserInfoResponse;
import foregg.foreggserver.dto.userDTO.UserHusbandJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserJoinRequestDTO;
import foregg.foreggserver.dto.userDTO.UserResponseDTO;

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

    public static User toHusband(KakaoUserInfoResponse userInfo, String keycode, User spouse, UserHusbandJoinRequestDTO dto) {

        return User.builder()
                .id(userInfo.getId())
                .keyCode(keycode)
                .email("동의 후 삽입")
                .nickname(userInfo.getProperties().getNickname())
                .ssn(dto.getSsn())
                .spouse(spouse)
                .build();
    }

    public static UserResponseDTO toUserResponseDTO(String keycode, String jwt) {
        return UserResponseDTO.builder()
                .keycode(keycode)
                .accessToken(jwt)
                .build();
    }
}
