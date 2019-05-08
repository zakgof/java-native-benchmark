package com.zakgof.jnbenchmark.jnr;

public class JnrBenchmark {
	
	public static short run() {
		SYSTEMTIME systemtime = new SYSTEMTIME(Kernel32.RUNTIME);
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond.shortValue();
	}

}
