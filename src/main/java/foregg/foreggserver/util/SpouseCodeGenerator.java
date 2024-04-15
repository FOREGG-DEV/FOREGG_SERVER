package foregg.foreggserver.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SpouseCodeGenerator {
    // 이전에 생성된 코드들을 저장할 Repository
    private static Set<String> randomCodeRepository = new HashSet<>();

    // 임의의 6자리 코드 생성 메소드
    public static String generateRandomCode() {
        // 영문 대소문자와 숫자를 포함한 문자열
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        // 문자열을 문자 배열로 변환
        char[] code = new char[6];
        Random random = new Random();
        String generatedCode;
        do {
            // 코드 생성
            for (int i = 0; i < 6; i++) {
                code[i] = characters.charAt(random.nextInt(characters.length()));
            }
            // 생성된 코드 문자열로 변환
            generatedCode = new String(code);
            // 중복 여부 확인
        } while (randomCodeRepository.contains(generatedCode)); // 중복된 코드가 있으면 다시 생성
        // 생성된 코드를 Repository에 추가
        randomCodeRepository.add(generatedCode);
        // 생성된 코드 반환
        return generatedCode;
    }

}
