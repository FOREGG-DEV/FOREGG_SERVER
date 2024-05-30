package foregg.foreggserver.dto.fcmDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//FCM에 전송하는 DTO
@Getter
@Builder
public class FcmMessageDTO {
    private boolean validateOnly;
    private FcmMessageDTO.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FcmMessageDTO.Notification data;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String type;
        private String targetId;
    }
}
