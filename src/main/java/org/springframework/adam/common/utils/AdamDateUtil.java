package org.springframework.adam.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;


public class AdamDateUtil extends DateUtils{

	/**
	 * 将Date类转换为XMLGregorianCalendar
	 * 
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar dateToXmlDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		DatatypeFactory dtf = null;
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
		}
		XMLGregorianCalendar dateType = dtf.newXMLGregorianCalendar();
		dateType.setYear(cal.get(Calendar.YEAR));
		// 由于Calendar.MONTH取值范围为0~11,需要加1
		dateType.setMonth(cal.get(Calendar.MONTH) + 1);
		dateType.setDay(cal.get(Calendar.DAY_OF_MONTH));
		dateType.setHour(cal.get(Calendar.HOUR_OF_DAY));
		dateType.setMinute(cal.get(Calendar.MINUTE));
		dateType.setSecond(cal.get(Calendar.SECOND));
		return dateType;
	}

	/**
	 * 将XMLGregorianCalendar转换为Date
	 * 
	 * @param cal
	 * @return
	 */
	public static Date xmlDate2Date(XMLGregorianCalendar cal) {
		return cal.toGregorianCalendar().getTime();
	}
	
	public static long getTimeMillis(Date date, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date curDate = sdf.parse(sdf2.format(date) + " " + time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
    /**
     * dateToString 格式化日期
     * 
     * @param date
     *            date
     * @param pattern
     *            pattern
     * @return String
     */
    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    
    /**
     * stringToDate 格式化日期
     * 
     * @param date
     *            date
     * @param pattern
     *            pattern
     * @return String
     * @throws ParseException 
     */
    public static Date stringToDate(String dateStr, String pattern) throws ParseException {
    	SimpleDateFormat format = new SimpleDateFormat(pattern);
    	return format.parse(dateStr);
    }
    
    /**
     * dateToString 格式化日期
     * 
     * @param date
     *            date
     * @param pattern
     *            pattern
     * @return String
     * @throws ParseException 
     */
    public static Date stringToDate(String dateStr) throws ParseException {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return format.parse(dateStr);
    }
    
    
}
