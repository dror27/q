package com.nightox.q.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.nightox.q.beans.Factory;

public class DateUtils {
	
	static private DateFormat		dateFormat = Factory.getInstance().getEnvironment().getApiDateFormat(null);

	static public String formatDate(Date date)
	{
		return dateFormat.format(date);
	}
	
	static public Date parseDate(String text) throws ParseException
	{
		return dateFormat.parse(text);
	}

	public static void sleep(long msec) 
	{
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
