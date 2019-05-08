package com.zakgof.jnbenchmark.bridj;

import org.bridj.Pointer;

public class BridjBenchmark {
	
	public static short run() {
		Kernel32 kernel32 = new Kernel32();
		SYSTEMTIME systime = new SYSTEMTIME();
		kernel32.getSystemTime(Pointer.getPointer(systime));
		return systime.wSecond();
	}
}
