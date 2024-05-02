package foregg.foreggserver.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

    public static String formatLocalDateTime(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    public static List<String> getIntervalDates(String startDateStr, String endDateStr) {
        List<String> dates = new ArrayList<>();

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.format(DateTimeFormatter.ISO_DATE));
            startDate = startDate.plusDays(1);

            // 윤년 체크
            boolean leapYear = startDate.isLeapYear();

            // 윤달 체크
            int lastDayOfMonth = startDate.lengthOfMonth();
            int currentDayOfMonth = startDate.getDayOfMonth();
            if (leapYear && startDate.getMonthValue() == 2 && currentDayOfMonth == 28) {
                startDate = startDate.plusDays(1); // 윤년이고 2월 28일인 경우 하루 추가
            } else if (currentDayOfMonth == lastDayOfMonth) {
                startDate = startDate.plusMonths(1).withDayOfMonth(1); // 다음달 1일로 이동
            }
        }
        return dates;
    }

    public static List<String> getWeekDates() {
        String dateStr = formatLocalDateTime(LocalDate.now());
        List<String> weekDates = new ArrayList<>();
        LocalDate date = LocalDate.parse(dateStr);

        // 해당 주의 시작일을 찾음 (일요일부터)
        LocalDate startOfWeek = date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        // 해당 주의 마지막일을 찾음 (토요일까지)
        LocalDate endOfWeek = date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));

        // 주의 시작일부터 마지막일까지 날짜를 리스트에 추가
        while (!startOfWeek.isAfter(endOfWeek)) {
            weekDates.add(startOfWeek.toString());
            startOfWeek = startOfWeek.plusDays(1);
        }

        return weekDates;
    }

}
