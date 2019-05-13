package com.zakgof.jnbenchmark.panama;

import java.foreign.Scope;
import java.foreign.memory.LayoutType;
import java.foreign.memory.Pointer;

import com.zakgof.jnbenchmark.panama.kernel32._SYSTEMTIME;

public class PanamaBenchmark {

	private final Scope scope;
	private LayoutType<_SYSTEMTIME> systemtimeLayout;
	private Pointer<_SYSTEMTIME> preallocatedSystemTime;

	public PanamaBenchmark() {
		scope = kernel32_h.scope().fork();
		systemtimeLayout = LayoutType.ofStruct(_SYSTEMTIME.class);
		preallocatedSystemTime = scope.allocate(systemtimeLayout);
	}

	public short all() {
		_SYSTEMTIME systemtime = scope.allocateStruct(_SYSTEMTIME.class);
		kernel32_h.GetSystemTime(systemtime.ptr());
		return systemtime.wSecond$get();
	}

	public short allWithPreLayout() {
		Pointer<_SYSTEMTIME> systemtime = scope.allocate(systemtimeLayout);
		kernel32_h.GetSystemTime(systemtime);
		return systemtime.get().wSecond$get();
	}

	public void callOnly() {
		kernel32_h.GetSystemTime(preallocatedSystemTime);
	}

}
