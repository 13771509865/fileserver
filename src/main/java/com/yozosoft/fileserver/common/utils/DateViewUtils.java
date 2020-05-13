package com.yozosoft.fileserver.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 处理日期的格式化
 */
public class DateViewUtils {

    /**
     * 确保线程安全
     */
    private static ThreadLocal<HashMap<String, SimpleDateFormat>> formatHolder = new ThreadLocal<HashMap<String, SimpleDateFormat>>();

    public static final String SIMPLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String FULL_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_PATTERN = "MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MMddHHmm = "MM-dd HH:mm";
    public static final String FORMAT_HOUR = "HH:mm";

    private static SimpleDateFormat getFormat(String key) {
        HashMap<String, SimpleDateFormat> map = formatHolder.get();
        if (map == null) {
            map = new HashMap<String, SimpleDateFormat>(2);
            formatHolder.set(map);// 保存回去
        }
        SimpleDateFormat simpleDateFormat = map.get(key);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(key);
            map.put(key, simpleDateFormat);
            formatHolder.set(map);// 保存回去
        }
        return simpleDateFormat;
    }

    /**
     * formatPattern 格式，date 日期
     */
    public static String formatDatePattern(Date date, String formatPattern) {
        if (date == null) {
            return "";
        }
        return getFormat(formatPattern).format(date);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(date);
    }

    public static String formatFullDate(Date date) {
        if (date == null) {
            return "";
        }
        return getFormat(FULL_DATE_FORMAT_PATTERN).format(date);
    }

//    public static String formatFullDate(long time) {
//        return getFormat(FULL_DATE_FORMAT_PATTERN).format(new Date(time));
//    }

    public static String formatFullDate(Long time) {
        if (time == null) {
            return "";
        }
        return getFormat(FULL_DATE_FORMAT_PATTERN).format(new Date(time));
    }

    public static String simpleFormatDate(Long time) {
        if (time == null) {
            return "";
        }
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(new Date(time));
    }

    public static String simpleFormatDate(Date date) {
        if (date == null) {
            return "";
        }
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(date);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return getFormat(pattern).format(date);
    }

    public static String formatFullDateToday(Date date) {
        if (date == null) {
            date = new Date(System.currentTimeMillis());
        }
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(date);
    }

    public static String formatHour(Date date) {
        if (date == null) {
            date = new Date(System.currentTimeMillis());
        }
        return getFormat(FORMAT_HOUR).format(date);
    }

    /**
     * 格式化稿件的日期
     *
     * @param date
     * @return
     */
    public static String formatDraftDate(Date date) {
        if (date == null) {
            date = new Date(System.currentTimeMillis());
        }
        return getFormat(DATE_FORMAT_MMddHHmm).format(date);
    }

    /**
     * 判断cal2是否在cal1之后，判断只精确到天
     *
     * <pre>
     *
     * isExpiredForDays(2011-11-4 ，2011-11-4 ) 返回0
     * isExpiredForDays(2011-11-4，2011-11-5 ) 返回小于0
     * isExpiredForDays(2011-11-5，2011-11-4 ) 返回大于0
     *
     * </pre>
     */
    public static int compareForDays(Calendar cal1, Calendar cal2) {
        if (isSameDay(cal1, cal2)) {
            return 0;
        }
        return cal1.compareTo(cal2);
    }

    public static int compareForDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return compareForDays(cal1, cal2);
    }

    public static int compareForDate(Date date1, Date date2) {
        return date1.compareTo(date2);
    }

    /**
     * 传入的日期是否已经过期了,只精确到天比较，且包含当天
     *
     * <pre>
     * 注意，判断时是采用currentTime > date 比较的。
     * 当前时间 2011-11-4 ,isExpiredForDays(2011-11-4 ) 返回false
     * 当前时间 2011-11-5 ,isExpiredForDays(2011-11-4 ) 返回true
     *
     * </pre>
     *
     * @param date 需要表的日期
     * @return true 如果已经过期返回<code>true</code>，如果传入的值是<code>null</code>也将返回
     * <code>true</code>,其他情况返回<code>false</code>
     */
    public static boolean isExpiredForDays(Date date) {
        if (date == null) {
            return true;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        if (cal1.compareTo(cal2) > 0) {// 当前时间比制定日期要大
            if (isSameDay(cal1, cal2)) {// 同一天的认为不过期
                return false;
            } else {
                return true;
            }
        } else {// 当前时间比制定日期要小
            return false;
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDayBefore(int before) {
        Date d = new Date();
        Date date = new Date(d.getTime() - before * 24 * 60 * 60 * 1000);
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(date);
    }

    public static String getNow() {
        Date date = new Date();
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(date);
    }

    public static Date getNowDate() {
        Date date = new Date();
        return date;
    }

    public static Date getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static String yesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(calendar.getTime());
    }

    /**
     * 获取昨天零点
     *
     * @author zhoufeng
     * @date 2019/7/30
     */
    public static String yesterdayFull() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return getFormat(FULL_DATE_FORMAT_PATTERN).format(calendar.getTime());
    }

    public static String nextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return getFormat(SIMPLE_DATE_FORMAT_PATTERN).format(calendar.getTime());
    }

    public static Date nextDayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public static String nextDayFull() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return getFormat(FULL_DATE_FORMAT_PATTERN).format(calendar.getTime());
    }

    public static String getNowFull() {
        Date date = new Date(System.currentTimeMillis());
        return getFormat(FULL_DATE_FORMAT_PATTERN).format(date);
    }

    /**
     * 将时间字符串(精确到时分秒)解析为Date对象。如果解析失败返回<code>null</code>
     */
    public static Date parseFull(String date) {
        if (date == null || "".equals(date)) {
            return null;
        }
        try {
            return getFormat(FULL_DATE_FORMAT_PATTERN).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将时间字符串(精确到天)解析为Date对象。如果解析失败返回<code>null</code>
     */
    public static Date parseSimple(String date) {
        try {
            return getFormat(SIMPLE_DATE_FORMAT_PATTERN).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将时间字符串(精确到天)解析为Date对象。如果解析失败返回<code>当前日期</code>
     */
    public static Date parseSimpleDate(String date) {
        try {
            return getFormat(SIMPLE_DATE_FORMAT_PATTERN).parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 特殊的pattern进行格式化处理
     */
    public static String format(String pattern, Date date) {
        if (pattern == null || "".equals(pattern)) {
            return formatDate(date);
        }
        return getFormat(pattern).format(date);

    }


    /**
     * 时间的文本显示
     *
     * @param time
     * @return
     */
    public static String getShowText(Date time) {
        if (time == null) {
            return "";
        }
        long between = Math.abs(System.currentTimeMillis() - time.getTime());
        // 60秒内,显示为刚刚
        if (between < 60 * 1000) {
            return "刚刚";
        } else {
            long min = (between / 1000 / 60) % 60;
            long hour = (between / 1000 / 60 / 60) % 24;
            long day = between / 1000 / 60 / 60 / 24;
            long month = day / 31;
            if (min > 0 && hour == 0 && day == 0 && month == 0) {
                return min + "分钟前";
            } else if (hour > 0 && day == 0 && month == 0) {
                return hour + "小时前";
            } else if (day > 0 && month == 0) {
                return day + "天前";
            } else if (month > 0) {
                return month + "个月前";
            }
        }
        return formatFullDate(time);
    }

    public static long differenceTimeMin(long time1, long time2) {
        long d = time1 - time2;
        long minute = d / (60 * 1000);
        return minute;

    }

    public static Date formatDateTime(Long date) {
        if (date == null) {
            return null;
        }
        String tmpDate = DateViewUtils.formatFullDate(date);
        if (!(date == null || "".equals(date))) {
            return DateViewUtils.parseFull(tmpDate);
        }
        return null;
    }

}
