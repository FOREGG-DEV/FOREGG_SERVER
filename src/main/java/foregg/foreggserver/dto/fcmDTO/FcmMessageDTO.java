package foregg.foreggserver.dto.fcmDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// FCM에 전송하는 DTO
@Getter
@Builder
public class FcmMessageDTO {
    private boolean validateOnly;
    private FcmMessageDTO.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FcmMessageDTO.Notification notification;
        private FcmMessageDTO.Data data;
        private String token;
        private FcmMessageDTO.Android android;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String type;
        private String targetId;
        private String time;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Android {
        private String priority;
    }
}
