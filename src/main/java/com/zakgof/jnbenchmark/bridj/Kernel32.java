package com.zakgof.jnbenchmark.bridj;

import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Runtime;

@Library("kernel32") 
@Runtime(CRuntime.class) 
public class Kernel32 {
	static {
		BridJ.register();
	}
	@Name("GetSystemTime") 
	public native void getSystemTime(Pointer<SYSTEMTIME> lpSystemTime);
}
