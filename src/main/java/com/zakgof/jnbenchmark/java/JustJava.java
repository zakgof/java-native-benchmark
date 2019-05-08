package com.zakgof.jnbenchmark.java;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class JustJava {

	public static int java_calendar() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}
	
	public static int java_ldt() {
		return LocalDateTime.now().getSecond();
	}
	
	@SuppressWarnings("deprecation")
	public static int java_date() {
		return new Date().getSeconds();
	}

}
