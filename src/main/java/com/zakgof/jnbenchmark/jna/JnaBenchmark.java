package com.zakgof.jnbenchmark.jna;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

public class JnaBenchmark {

	private final WinBase.SYSTEMTIME preallocatedSystemtime;

	public JnaBenchmark() {
		preallocatedSystemtime = new WinBase.SYSTEMTIME();
	}

	public void callOnly() {
		Kernel32.INSTANCE.GetSystemTime(preallocatedSystemtime);
	}

	public static short all() {
		WinBase.SYSTEMTIME systemtime = new WinBase.SYSTEMTIME();
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond;
	}
}
