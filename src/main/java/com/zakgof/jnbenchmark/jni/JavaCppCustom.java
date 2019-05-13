package com.zakgof.jnbenchmark.jni;

import com.zakgof.jnbenchmark.jni.gen.Kernel32Library;

public class JavaCppCustom {

	Kernel32Library.SYSTEMTIME preallocatedSystemTime;

	public JavaCppCustom() {
		preallocatedSystemTime = new Kernel32Library.SYSTEMTIME();
	}

	public void callOnly() {
		Kernel32Library.GetSystemTime(preallocatedSystemTime);
	}

	public static short all() {
		Kernel32Library.SYSTEMTIME systemtime = new Kernel32Library.SYSTEMTIME();
		Kernel32Library.GetSystemTime(systemtime);
		return systemtime.wSecond();
	}
}
