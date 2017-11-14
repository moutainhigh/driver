package com.easymi.component.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static final String HM = "HH:mm";
    public static final String HMS = "HH:mm:ss";
    public static final String MD_HM = "MM-dd HH:mm";
    public static final String MD_HMS = "MM-dd HH:mm:ss";
    static final int NEXT_CLOCK = 0x7;
    static final int NEXT_MINUTE = 0x0;
    public static final String SERVER_FORMAT = "yyyyMMddHHmmss";
    public static final String YM = "yyyy-MM";
    public static final String YM2 = "yyyyMM";
    public static final String YMD = "yyyyMMdd";
    public static final String YMD_2 = "yyyy-MM-dd";
    public static final String YMD_3 = "yyyy_MM_dd";
    public static final String YMD_4_CN = "yyyy年MM月dd日";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_HM_LOG = "yyyyMMdd_HHmm";

    @SuppressLint("SimpleDateFormat")
    public static long parseTime(String format, String time) {
        Date date = null;
        try {
            if (time != null) {

                date = new SimpleDateFormat(format).parse(time);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : System.currentTimeMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String format, long time) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    public static boolean isExpired(long savedExprieTime) {
        if (savedExprieTime == 0x0) {
            return true;
        }
        long curTime = parseTime("yyyyMMdd", getTime("yyyyMMdd", System.currentTimeMillis()));
        if (savedExprieTime <= curTime) {
            return false;
        }
        return true;
    }

    public static boolean isExpired(String checkTime) {
        if (TextUtils.isEmpty(checkTime)) {
            return true;
        }
        long time = parseTime("yyyyMMdd", checkTime);
        String now = getTime("yyyyMMdd", System.currentTimeMillis());
        long curTime = parseTime("yyyyMMdd", now);
        if (time <= curTime) {
            return false;
        }
        return true;
    }

    public static String getTimeSpan(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "\u5929");
        }
        if (hours > 0) {
            buffer.append(hours + "\u5c0f\u65f6");
        }
        if (min > 0) {
            buffer.append(min + "\u5206\u949f");
        }
        return buffer.toString();
    }

    public static String getTimeFromMinutes(int minutes) {

        StringBuilder buffer = new StringBuilder();

        int min = minutes % 0x3c;
        int hours = (minutes / 0x3c) % 0x3c;
        if (hours > 0) {
            buffer.append(hours + "\u5c0f\u65f6");
        }
        if (min >= 0) {
            buffer.append(min + "\u5206\u949f");
        }

        return buffer.toString();
    }

    public static String getTimeSpanSeconds(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int sec = seconds % 0x3c;
        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "D");
        }
        if (hours > 0) {
            buffer.append(hours + "h");
        }
        if (min > 0) {
            buffer.append(min + "m");
        }
        if (sec >= 0) {
            buffer.append(sec + "s");
        }
        return buffer.toString();
    }

    public static String getTimeSpanMinutes(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "D");
        }
        if (hours > 0) {
            buffer.append(hours + "h");
        }
        if (min >= 0) {
            buffer.append(min + "m");
        }
        return buffer.toString();
    }

    public static long getTime(String date, int hour, int minute) {
        return parseTime("yyyy-MM-dd HH:mm", date + " " + String.format("%1$,02d:%2$,02d", hour, minute));
    }

    public static boolean isHourValid(String hour) {
        if (null == hour) {
            return false;
        }
        try {
            Integer h = Integer.parseInt(hour);
            if (h < 0 || h > 23) {
                return false;
            }

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean isMinuteValid(String minute) {
        if (null == minute) {
            return false;
        }
        try {
            Integer m = Integer.parseInt(minute);
            if (m < 0 || m > 59) {
                return false;
            }

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    @SuppressLint("DefaultLocale")
    public static float convertMinutesToHours(float minutes) {
        float hours = minutes / 60.0f;
        try {
            return Float.parseFloat(String.format("%.1f", Float.valueOf(hours)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0x0;
    }

    public static Calendar getNextSevenHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(0xb);
        if (hourOfDay >= 0x7) {
            calendar.set(0x6, (calendar.get(0x6) + 0x1));
        }
        calendar.set(0xb, 0x7);
        calendar.set(0xc, 0x0);
        calendar.set(0xd, 0x0);
        return calendar;
    }

    public static Calendar getPreviewSevenHourCalendar() {
        Calendar sevenClockCal = Calendar.getInstance();
        int hourOfDay = sevenClockCal.get(0xb);
        if (hourOfDay < 0x7) {
            sevenClockCal.set(0x6, (sevenClockCal.get(0x6) - 0x1));
        }
        sevenClockCal.set(0xb, 0x7);
        sevenClockCal.set(0xc, 0x0);
        sevenClockCal.set(0xd, 0x0);
        return sevenClockCal;
    }

    public static long getNextSevenHourOfDayDelayMillis() {
        Calendar calendar = getNextSevenHourCalendar();
        return (calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public static String convertMillisTime(long millisSeconds) {
        return convertSecondsTime((millisSeconds / 0x3e8));
    }

    @SuppressLint("DefaultLocale")
    public static String convertSecondsTime(long seconds) {
        int minute = (int) (seconds / 0x3c);
        int hour = minute / 0x3c;
        int second = (int) (seconds % 0x3c);
        minute = minute % 0x3c;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static int converSeconds2Minutes(int seconds) {
        return (seconds / 0x3c);
    }

    public static int convertMinutes2Seconds(int minutes) {
        return (minutes * 0x3c);
    }

    public static String getTimeSpanMinuteSeconds(int seconds) {
        int sec = seconds % 0x3c;
        int min = seconds / 0x3c;
        StringBuffer buffer = new StringBuffer();
        buffer.append(min).append("\u5206").append(sec).append("\u79d2");
        return buffer.toString();
    }

    public static int convertSeconds2MinutesRound(int seconds) {
        int min = seconds / 0x3c;
        int sec = seconds % 0x3c;
        if (sec >= 0x1e) {
            min = min + 0x1;
        }
        return min;
    }

    public static String now() {
        return String.valueOf(System.currentTimeMillis());
    }
}