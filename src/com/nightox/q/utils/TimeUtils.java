package com.nightox.q.utils;

public class TimeUtils {

	static public void delay(long msec)
	{
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}
}
