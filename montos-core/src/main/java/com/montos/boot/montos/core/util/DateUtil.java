package com.jimistore.boot.nemo.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

public class DateUtil {
	
	public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	@Value("${spring.mvc.date-format:}")
	public static void setDateFormat(String dateFormat) {
		if(dateFormat.length()>0){
			DateUtil.dateFormat = dateFormat;
		}
	}

	@Value("${spring.jackson.date-format:}")
	public static void setDateTimeFormat(String dateFormat) {
		if(dateFormat.length()>0){
			DateUtil.dateFormat = dateFormat;
		}
	}

	/**
	 * 得到当前日期
	 * @return
	 */
	public static java.sql.Date getNowWithSqlDate(){
		return new java.sql.Date(Calendar.getInstance().getTimeInMillis());
	}
	
	public static Date getNowTime(){
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
		String source=sdf.format(Calendar.getInstance().getTime());
		try{
			return sdf.parse(source);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到日期字符串
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date)
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	/**
	 * 根据解析字符串得到时间字符串
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date,String formatStr)
	{
		return new SimpleDateFormat(formatStr).format(date);
	}
	
	/**
	 * 得到日期时间字符串
	 * @param date
	 * @return
	 */
	public static String getDateTimeStr(Date date)
	{
		return new SimpleDateFormat(dateFormat).format(date);
	}
	
	/**
	 * 得到时间字符串
	 * @param date
	 * @return
	 */
	public static String getTimeStr(Date date)
	{
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
	
	/**
	 * 得到当前时间的字符串
	 * @return
	 */
	public static String getDateTimeStr()
	{
		return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
	}
	
	/**
	 * 得到当前时间的字符串
	 * @return
	 */
	public static String getDateStr()
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * 得到当前时间的字符串
	 * @return
	 */
	public static String getTimeStr()
	{
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * 根据时间字符串(标准格式)转换日期
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseDateTime(String source) throws ParseException
	{
		return new SimpleDateFormat(dateFormat).parse(source);
	}
	
	/**
	 * 根据时间字符串和时间格式转换日期
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseDateTime(String source,String format) throws ParseException
	{
		return new SimpleDateFormat(format).parse(source);
	}
}
