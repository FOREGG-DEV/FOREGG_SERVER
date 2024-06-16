package foregg.foreggserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static List<String> parseString(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result;
        }

        // 정규 표현식 패턴을 정의합니다.
        Pattern pattern = Pattern.compile("#[^#]*");
        Matcher matcher = pattern.matcher(input);

        // 패턴과 일치하는 부분을 찾아 리스트에 추가합니다.
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }
}
