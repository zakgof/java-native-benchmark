package com.zakgof.jnbenchmark.jnr;

import jnr.ffi.CallingConvention;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;

public interface Kernel32 {
	
	public static final Kernel32 INSTANCE = LibraryLoader.create(Kernel32.class)
            .convention(CallingConvention.STDCALL)
            .load("Kernel32");
	
	public static final Runtime RUNTIME = Runtime.getRuntime(INSTANCE);
	
    int GetSystemTime(SYSTEMTIME pSystemTime);
    
}