package com.zakgof.jnbenchmark.jnr;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class SYSTEMTIME extends Struct {
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