package com.zakgof.jnbenchmark.jni;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.windows;
import org.bytedeco.javacpp.windows.SYSTEMTIME;

public class JavaCppStock {

	private static final long SYSTEMTIME_STRUCT_LENGTH = 16; // precalculated as new SYSTEMTIME().sizeof();
	private SYSTEMTIME preallocatedSystemTime;

	public JavaCppStock() {
		preallocatedSystemTime = new SYSTEMTIME();
	}

	public void callOnly() {
		windows.GetSystemTime(preallocatedSystemTime);
	}

	public static short all() {
		SYSTEMTIME systemtime = new SYSTEMTIME(Pointer.malloc(SYSTEMTIME_STRUCT_LENGTH)); // new SYSTEMTIME() is much slower, see https://github.com/bytedeco/javacpp/issues/299
		windows.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}

}
