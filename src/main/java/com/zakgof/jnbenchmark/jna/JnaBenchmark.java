package com.zakgof.jnbenchmark.jna;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

public class JnaBenchmark {
	
	public static short jna() {
		WinBase.SYSTEMTIME systemtime = new WinBase.SYSTEMTIME();
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond;
	}
}
