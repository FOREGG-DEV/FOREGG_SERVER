package foregg.foreggserver.converter;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;

public class JsonConverter {

    public static String convertToJson(Object dto) {
        // Gson 객체 생성
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // JSON 형식으로 변환하여 반환
        return gson.toJson(new Response(dto));
    }

    // 응답 모델 클래스
    private static class Response {
        private Object data;

        public Response(Object data) {
            this.data = data;
        }
    }
}