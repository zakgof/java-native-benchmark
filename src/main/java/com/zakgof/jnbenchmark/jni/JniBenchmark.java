package com.zakgof.jnbenchmark.jni;

import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.SYSTEMTIME;

public class JniBenchmark {

	public static short run() {
		SYSTEMTIME systemtime = new SYSTEMTIME();
		windows.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}

}
