package com.lakecloud.manage.seller.Tools;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	public static String getDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public static String getDate(Timestamp ts) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(ts);
	}

	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date());
	}

	/**
	 * 将字符串转换为日期
	 * 
	 * @param time
	 *            日期字符
	 * @return 日期
	 */
	public static Date stringToDate(String time) {
		Date date = null;
		if (time != null && !"".equals(time)) {
			time = time.trim();
			String format = "";
			if (time.indexOf("-") > -1) {
				format = "yyyy-MM-dd" + appendTime(time);
			} else if (time.indexOf("/") > -1) {
				format = "yyyy/MM/dd" + appendTime(time);
			}
			try {
				date = new SimpleDateFormat(format).parse(time,
						new ParsePosition(0));
			} catch (Exception e) {
			}
		}
		return date;
	}

	/**
	 * 判断日期字符串的格式
	 * 
	 * @param time
	 *            time
	 * @return time
	 */
	private static String appendTime(String time) {
		String fmt = "";
		if (time.indexOf(":") > -1) {
			String[] ts = time.split(":");
			switch (ts.length) {
			case 1:
				fmt = " HH";
				break;
			case 2:
				fmt = " HH:mm";
				break;
			case 3:
				fmt = " HH:mm:ss";
				break;
			default:
				break;
			}
		} else if (time.indexOf(" ") > -1) {
			fmt = " HH";
		}
		return fmt;
	}

	public static Timestamp string2Timestamp(String time) {
		return new Timestamp(stringToDate(time).getTime());
	}
}
