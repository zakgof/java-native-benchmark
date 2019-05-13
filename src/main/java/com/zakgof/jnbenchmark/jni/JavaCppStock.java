package com.zakgof.jnbenchmark.jni;

import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.SYSTEMTIME;

public class JavaCppStock {

	private SYSTEMTIME preallocatedSystemTime;

	public JavaCppStock() {
		preallocatedSystemTime = new SYSTEMTIME();
	}

	public void callOnly() {
		windows.GetSystemTime(preallocatedSystemTime);
	}

	public static short all() {
		SYSTEMTIME systemtime = new SYSTEMTIME();
		windows.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}

}
