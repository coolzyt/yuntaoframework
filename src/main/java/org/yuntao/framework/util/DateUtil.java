package org.yuntao.framework.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
public class DateUtil {
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static final DateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private DateUtil() {
    }

    /*
     * Date --> String
     */
    public synchronized static String date2String(Date date, DateFormat dateFormat) {
        if(date==null) return null;
        return dateFormat.format(date);
    }

    public static String date2String(Date date, String dateFormat) {
        if(date==null) return null;
        return date2String(date, new SimpleDateFormat(dateFormat));
    }

    public static String date2String(Date date) {
        if(date==null) return null;
        return date2String(date, DEFAULT_DATE_FORMAT);
    }

    public synchronized static String time2String(Date time, DateFormat dateFormat) {
        if(time==null) return null;
        return dateFormat.format(time);
    }

    public static String time2String(Date time, String dateFormat) {
        if(time==null) return null;
        return date2String(time, new SimpleDateFormat(dateFormat));
    }

    public static String time2String(Date time) {
        if(time==null) return null;
        return date2String(time, DEFAULT_TIME_FORMAT);
    }

    public synchronized static String dateTime2String(Date dateTime, DateFormat dateFormat) {
        if(dateTime==null) return null;
        return dateFormat.format(dateTime);
    }

    public static String dateTime2String(Date dateTime, String dateFormat) {
        if(dateTime==null) return null;
        return date2String(dateTime,dateFormat);
    }

    public static String dateTime2String(Date dateTime) {
        if(dateTime==null) return null;
        return date2String(dateTime, DEFAULT_DATETIME_FORMAT);
    }

    /*
     * String -->Date
     */

    public synchronized static Date string2Date(String date, DateFormat dateFormat) {
        if(date==null) return null;
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date string2Date(String date, String dateFormat) {
        return string2Date(date, new SimpleDateFormat(dateFormat));
    }

    public static Date string2Date(String date) {
        return string2Date(date, DEFAULT_DATE_FORMAT);
    }

    public static Time string2Time(String time, DateFormat timeFormat) {
        if(time==null) return null;
        return new Time(string2Date(time, timeFormat).getTime());
    }

    public static Time string2Time(String time, String timeFormat) {
        if(time==null) return null;
        return new Time(string2Date(time, timeFormat).getTime());
    }

    public static Time string2Time(String time) {
        return string2Time(time, DEFAULT_TIME_FORMAT);
    }

    public static Timestamp string2DateTime(String time, DateFormat timeFormat) {
        if(time==null) return null;
        return new Timestamp(string2Date(time, timeFormat).getTime());
    }

    public static Timestamp string2DateTime(String time, String timeFormat) {
        if(time==null) return null;
        return new Timestamp(string2Date(time, timeFormat).getTime());
    }

    public static Timestamp string2DateTime(String time) {
        return string2DateTime(time, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 取得当前日期。日期格式为：yyyy-MM-dd 
     * @return 当前日期字符串。
     */
    public synchronized static String getCurrentDateAsString() {
        return DEFAULT_DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    /**
     * 取得当前日期时间。日期格式为：yyyy-MM-dd hh:mm:ss     * 
     * @return 当前日期字符串。
     */
    public synchronized static String getCurrentDateTimeAsString() {
        return DEFAULT_DATETIME_FORMAT.format(Calendar.getInstance().getTime());
    }

    /**
     * 取得当前日期时间。日期格式为由dateFormat定义
     * 
     * @param dateFormat 格式串
     * @return 当前日期字符串。
     */
    public static String getCurrentDateAsString(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 根据dateFormat定义日期格式取得指定的日期
     * 
     * @param date
     *            指定的日期
     * @param dateFormat
     *            格式串
     * @return 日期字符串。
     */
    public static String getDateString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    public static Date parseDate(String date, DateFormat df) {
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 根据dateFormat定义日期格式取得指定的日期
     * 
     * @param date
     *            指定的日期字符串
     * @param dateFormat
     *            格式串
     * @return 日期
     */
    public static Date parseDate(String date, String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        return parseDate(date, fmt);
    }

    public synchronized static Date parseDate(String date) {
        return parseDate(date, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 取得当前的时间戳
     * 
     * @return 时间戳
     */
    public static Timestamp nowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 将指定的日期转换时间戳     * 
     * @param date
     *            日期
     * @return 时间戳
     */
    public static Timestamp toTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static String toString(Date time) {
        return getDateString(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String fromUnixTime(Long ms) {
        return getDateString(new Date(ms.longValue() * 1000), "yyyy-MM-dd HH:mm:ss");
    }

    public static Long unixTimestamp(String date) {
        return new Long(parseDate(date).getTime() / 1000);
    }

    /**
     * 根据给定格式生成数字型的日期
     * 
     * @param date
     * @param dateFormat
     * @return 数字日期
     */
    public static Long unixTimestamp(String date, String dateFormat) {
        return new Long(parseDate(date, dateFormat).getTime() / 1000);
    }

    public static Long currentUnixTimestamp() {
        return new Long(System.currentTimeMillis() / 1000);
    }

    public static Long unixTimestamp(Date date) {
        return new Long(date.getTime() / 1000);
    }

}
