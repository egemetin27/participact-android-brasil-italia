package com.bergmannsoft.util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by fabiobergmann on 11/01/17.
 */

public class CalendarUtils {

    public static List<String> getAllDays() {
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add((i < 10 ? "0" : "") + String.valueOf(i));
        }
        return days;
    }

    public static List<String> getAllYears() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int year = cal.get(YEAR);

        List<String> days = new ArrayList<>();
        for (int i = year; i > year - 140; i--) {
            days.add(String.valueOf(i));
        }
        return days;
    }

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        return cal.get(YEAR);
    }

//    public static List<String> getAllMonths(Context context) {
//        List<String> days = Arrays.asList(
//                context.getString(R.string.january),
//                context.getString(R.string.february),
//                context.getString(R.string.march),
//                context.getString(R.string.april),
//                context.getString(R.string.may),
//                context.getString(R.string.june),
//                context.getString(R.string.july),
//                context.getString(R.string.august),
//                context.getString(R.string.september),
//                context.getString(R.string.october),
//                context.getString(R.string.november),
//                context.getString(R.string.december)
//        );
//        return new ArrayList<>(days);
//    }

//    public static String getMonthNumberFormat(Context context, String name) {
//        if (name == null) {
//            return "";
//        } else if (name.equals(context.getString(R.string.january))) {
//            return "01";
//        } else if (name.equals(context.getString(R.string.february))) {
//            return "02";
//        } else if (name.equals(context.getString(R.string.march))) {
//            return "03";
//        } else if (name.equals(context.getString(R.string.april))) {
//            return "04";
//        } else if (name.equals(context.getString(R.string.may))) {
//            return "05";
//        } else if (name.equals(context.getString(R.string.june))) {
//            return "06";
//        } else if (name.equals(context.getString(R.string.july))) {
//            return "07";
//        } else if (name.equals(context.getString(R.string.august))) {
//            return "08";
//        } else if (name.equals(context.getString(R.string.september))) {
//            return "09";
//        } else if (name.equals(context.getString(R.string.october))) {
//            return "10";
//        } else if (name.equals(context.getString(R.string.november))) {
//            return "11";
//        } else if (name.equals(context.getString(R.string.december))) {
//            return "12";
//        } else {
//            return "";
//        }
//    }

    public static int getAge(String dateStr, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            Date date = format.parse(dateStr);
            System.out.println(date);
            return getDiffYears(date, new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static int getDiffMonths(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = (b.get(MONTH) - a.get(MONTH)) + (getDiffYears(first, last) * 12);
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date toDate(String month, String year) {
        if (NumberUtils.isNumber(month) && NumberUtils.isInteger(year)) {
            return toDate(Integer.parseInt(month) - 1, Integer.parseInt(year));
        }
        return new Date();
    }

    public static Date toDate(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

//    public static String getMonthName(Context context, Date date) {
//        Calendar cal = getCalendar(date);
//        int month = cal.get(MONTH) + 1;
//        switch (month) {
//            case 1:
//                return context.getString(R.string.january);
//            case 2:
//                return context.getString(R.string.february);
//            case 3:
//                return context.getString(R.string.march);
//            case 4:
//                return context.getString(R.string.april);
//            case 5:
//                return context.getString(R.string.may);
//            case 6:
//                return context.getString(R.string.june);
//            case 7:
//                return context.getString(R.string.july);
//            case 8:
//                return context.getString(R.string.august);
//            case 9:
//                return context.getString(R.string.september);
//            case 10:
//                return context.getString(R.string.october);
//            case 11:
//                return context.getString(R.string.november);
//            case 12:
//                return context.getString(R.string.december);
//        }
//        return "";
//    }

    public static int getYear(Date date) {
        Calendar cal = getCalendar(date);
        return cal.get(YEAR);
    }

    public static int getMonth(Date date) {
        Calendar cal = getCalendar(date);
        return cal.get(MONTH) + 1;
    }

    public static Date dateByAddingDays(int days) {
        Calendar cal = getCalendar(new Date());
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

}
