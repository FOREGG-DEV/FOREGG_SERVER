package foregg.foreggserver.util;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
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
        List<String> weekDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        weekDates.add(String.valueOf(startOfWeek.minusDays(1)));

        while (!startOfWeek.isAfter(endOfWeek)) {
            weekDates.add(startOfWeek.toString());
            startOfWeek = startOfWeek.plusDays(1);
        }
        weekDates.remove(7);
        return weekDates;
    }

    public static List<String> getPast30Days(String endDateStr) {
        // 끝 날짜를 LocalDate 객체로 변환
        LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_DATE);

        // 시작 날짜 계산 (끝 날짜에서 30일 전)
        LocalDate startDate = endDate.minusDays(30);

        // 날짜들을 저장할 리스트 생성
        List<String> dates = new ArrayList<>();

        // 시작 날짜부터 끝 날짜까지 하루씩 증가하면서 리스트에 추가
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate.format(DateTimeFormatter.ISO_DATE));
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

    public static boolean extractSameYearmonth(String date1Str, String date2Str) {
        // 첫 번째와 두 번째 날짜를 LocalDate 객체로 변환
        LocalDate date1 = LocalDate.parse(date1Str + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate date2 = LocalDate.parse(date2Str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 연도와 월이 일치하는지 확인
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }

    public static List<String> convertDatesToDayOfWeek(List<String> dates) {
        List<String> dayOfWeekList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String date : dates) {
            LocalDate localDate = LocalDate.parse(date, formatter);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            String dayOfWeekString = dayOfWeek.toString();
            // 한글 요일로 변환
            switch (dayOfWeekString) {
                case "MONDAY":
                    dayOfWeekString = "월";
                    break;
                case "TUESDAY":
                    dayOfWeekString = "화";
                    break;
                case "WEDNESDAY":
                    dayOfWeekString = "수";
                    break;
                case "THURSDAY":
                    dayOfWeekString = "목";
                    break;
                case "FRIDAY":
                    dayOfWeekString = "금";
                    break;
                case "SATURDAY":
                    dayOfWeekString = "토";
                    break;
                case "SUNDAY":
                    dayOfWeekString = "일";
                    break;
            }
            dayOfWeekList.add(dayOfWeekString);
        }

        return dayOfWeekList;
    }

    public static String getWeekOfMonth(String dateStr) {
        // 입력된 날짜 문자열을 LocalDate로 변환
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 해당 날짜의 월과 주차를 계산
        int month = date.getMonthValue();
        int weekOfMonth = (date.getDayOfMonth() - 1) / 7 + 1;

        return month + "월 " + weekOfMonth + "주차";
    }


}
