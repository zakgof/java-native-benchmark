package com.zakgof.jnbenchmark;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.SYSTEMTIME;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

public class Main {

	public static short jni_javacpp() {
		SYSTEMTIME systemtime = new SYSTEMTIME();
		windows.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}
	
	public static short jna() {
		WinBase.SYSTEMTIME systemtime = new WinBase.SYSTEMTIME();
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond;
	}

	public static int java_calendar() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}
	
	public static int java_ldt() {
		return LocalDateTime.now().getSecond();
	}

}
