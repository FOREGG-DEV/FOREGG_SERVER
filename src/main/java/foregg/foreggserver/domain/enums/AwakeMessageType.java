package foregg.foreggserver.domain.enums;

import java.util.Random;

public enum AwakeMessageType {
    MESSAGE1("님 다음주는 배우자와 어떤 일을 계획 중이신가요?", NavigationType.calendar_graph.toString()),
    MESSAGE2("님 나의 지원금을 점검해보세요!", NavigationType.account_graph.toString()),
    MESSAGE3("님 허그는 부부가 같이 쓰는 거래요!", NavigationType.daily_hugg_graph.toString()),
    MESSAGE4("MESSAGE4", null);

    private final String message;
    private final String pairMessage; // 짝꿍 메시지

    AwakeMessageType(String message, String pairMessage) {
        this.message = message;
        this.pairMessage = pairMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getPairMessage() {
        return pairMessage;
    }

    // 랜덤한 메시지와 그 짝꿍 메시지를 함께 반환
    public static String[] getRandomMessagePair() {
        AwakeMessageType[] values = AwakeMessageType.values();
        AwakeMessageType selected = values[new Random().nextInt(values.length)];
        return new String[]{selected.getMessage(), selected.getPairMessage()};
    }

    public static void main(String[] args) {
        String[] messages = getRandomMessagePair();
        System.out.println("메인 메시지: " + messages[0]);
        System.out.println("짝꿍 메시지: " + messages[1]);
    }
}

