package com.zakgof.jnbenchmark.jnr;

public class JnrBenchmark {
	
	private SYSTEMTIME preallocatedSystemTime;

	public JnrBenchmark() {
		preallocatedSystemTime = new SYSTEMTIME(Kernel32.RUNTIME);
	}
	
	public void callOnly() {
		Kernel32.INSTANCE.GetSystemTime(preallocatedSystemTime);
	}
	
	public static short all() {
		SYSTEMTIME systemtime = new SYSTEMTIME(Kernel32.RUNTIME);
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond.shortValue();
	}

}
