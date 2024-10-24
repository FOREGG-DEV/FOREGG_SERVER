package foregg.foreggserver.dto.fcmDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// FCM에 전송하는 DTO
@Getter
@Builder
public class FcmMessageDTO {
    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Data data;
        private String token;
        private Android android;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String title;
        private String body;
        private String type;
        private String targetId;
        private String time;
        private String vibration;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Android {
        private String priority;
    }
}
