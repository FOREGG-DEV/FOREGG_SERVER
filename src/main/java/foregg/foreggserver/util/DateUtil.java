package foregg.foreggserver.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateUtil {

    public static String getYearAndMonth(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        // 년월까지의 부분을 추출
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public static List<String> getMonthsBetween(String startDateString, String endDateString) {
        List<String> monthsBetween = new ArrayList<>();

        LocalDate startDate = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(endDateString, DateTimeFormatter.ISO_LOCAL_DATE);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String yearMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (!monthsBetween.contains(yearMonth)) {
                monthsBetween.add(yearMonth);
            }
            // 다음 달로 이동
            currentDate = currentDate.plusMonths(1);
        }

        return monthsBetween;
    }

    public static List<String> getAdjacentMonths(String monthString) {
        List<String> adjacentMonths = new ArrayList<>();

        LocalDate month = LocalDate.parse(monthString + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 입력된 월을 먼저 추가
        String inputMonth = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        adjacentMonths.add(inputMonth);

        // 입력된 월의 한 달 전의 연월을 추출
        LocalDate previousMonthDate = month.minusMonths(1);
        String previousMonth = previousMonthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        adjacentMonths.add(previousMonth);

        // 입력된 월의 한 달 후의 연월을 추출
        LocalDate nextMonthDate = month.plusMonths(1);
        String nextMonth = nextMonthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        adjacentMonths.add(nextMonth);

        // 날짜 순으로 정렬
        Collections.sort(adjacentMonths);

        return adjacentMonths;
    }


}
