package com.zakgof.jnbenchmark.jni;

import org.bytedeco.javacpp.Pointer;

import com.zakgof.jnbenchmark.jni.gen.Kernel32Library;

public class JavaCppCustom {

	private static final long SYSTEMTIME_STRUCT_LENGTH = 16; // precalculated as new SYSTEMTIME().sizeof();
	private final Kernel32Library.SYSTEMTIME preallocatedSystemTime;

	public JavaCppCustom() {
		preallocatedSystemTime = new Kernel32Library.SYSTEMTIME();
	}

	public void callOnly() {
		Kernel32Library.GetSystemTime(preallocatedSystemTime);
	}

	public static short all() {
		Kernel32Library.SYSTEMTIME systemtime = new Kernel32Library.SYSTEMTIME(Pointer.malloc(SYSTEMTIME_STRUCT_LENGTH));
		Kernel32Library.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}
}
