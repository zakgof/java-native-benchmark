package com.zakgof.jnbenchmark.jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;

public class JnaDirectBenchmark {

	static {
		Native.register("kernel32");
	}

	private WinBase.SYSTEMTIME preallocatedSystemtime;

	native static void GetSystemTime(SYSTEMTIME lpSystemTime);

	public JnaDirectBenchmark() {
		preallocatedSystemtime = new WinBase.SYSTEMTIME();
	}

	public void callOnly() {
		GetSystemTime(preallocatedSystemtime);
	}

	public static short all() {
		WinBase.SYSTEMTIME systemtime = new WinBase.SYSTEMTIME();
		GetSystemTime(systemtime);
		return systemtime.wSecond;
	}

}
