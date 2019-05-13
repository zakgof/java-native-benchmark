package com.zakgof.jnbenchmark.bridj;

import org.bridj.Pointer;

public class BridjBenchmark {
	
	private Pointer<SYSTEMTIME> preallocatedPointer;
	private static Kernel32 kernel32 = new Kernel32();

	public BridjBenchmark() {
		SYSTEMTIME systemtime = new SYSTEMTIME();
		preallocatedPointer = Pointer.getPointer(systemtime);
	}
	
	public void callOnly() {
		kernel32.getSystemTime(preallocatedPointer);
	}
	
	public static short all() {
		SYSTEMTIME systime = new SYSTEMTIME();
		kernel32.getSystemTime(Pointer.getPointer(systime));
		return systime.wSecond();
	}
}
