package com.zakgof.jnbenchmark;

import jnr.ffi.CallingConvention;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class Jnr {
	
	public static class SYSTEMTIME extends Struct {
		protected SYSTEMTIME(Runtime runtime) {
			super(runtime);
		}
		public final WORD wYear = new WORD();
		public final WORD wMonth = new WORD();
		public final WORD wDayOfWeek = new WORD();
		public final WORD wDay = new WORD();
		public final WORD wHour = new WORD();
		public final WORD wMinute = new WORD();
		public final WORD wSecond = new WORD();
		public final WORD wMilliseconds = new WORD();
	}
	
	public static interface Kernel32 {
		
		public static final Kernel32 INSTANCE = LibraryLoader.create(Kernel32.class)
                .convention(CallingConvention.STDCALL)
                .load("Kernel32");
		
		public static final Runtime RUNTIME = Runtime.getRuntime(INSTANCE);
		
        int GetSystemTime(SYSTEMTIME pSystemTime);
        
        
    }
	
	public static short run() {
		SYSTEMTIME systemtime = new SYSTEMTIME(Kernel32.RUNTIME);
		Kernel32.INSTANCE.GetSystemTime(systemtime);
		return systemtime.wSecond.shortValue();
	}

}
