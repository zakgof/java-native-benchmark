package com.zakgof.jnbenchmark.bridj;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

@Library("kernel32") 
public class SYSTEMTIME extends StructObject {
	static {
		BridJ.register();
	}
	@Field(0) 
	public short wYear() {
		return this.io.getShortField(this, 0);
	}
	@Field(0) 
	public SYSTEMTIME wYear(short wYear) {
		this.io.setShortField(this, 0, wYear);
		return this;
	}
	@Field(1) 
	public short wMonth() {
		return this.io.getShortField(this, 1);
	}
	@Field(1) 
	public SYSTEMTIME wMonth(short wMonth) {
		this.io.setShortField(this, 1, wMonth);
		return this;
	}
	@Field(2) 
	public short wDayOfWeek() {
		return this.io.getShortField(this, 2);
	}
	@Field(2) 
	public SYSTEMTIME wDayOfWeek(short wDayOfWeek) {
		this.io.setShortField(this, 2, wDayOfWeek);
		return this;
	}
	@Field(3) 
	public short wDay() {
		return this.io.getShortField(this, 3);
	}
	@Field(3) 
	public SYSTEMTIME wDay(short wDay) {
		this.io.setShortField(this, 3, wDay);
		return this;
	}
	@Field(4) 
	public short wHour() {
		return this.io.getShortField(this, 4);
	}
	@Field(4) 
	public SYSTEMTIME wHour(short wHour) {
		this.io.setShortField(this, 4, wHour);
		return this;
	}
	@Field(5) 
	public short wMinute() {
		return this.io.getShortField(this, 5);
	}
	@Field(5) 
	public SYSTEMTIME wMinute(short wMinute) {
		this.io.setShortField(this, 5, wMinute);
		return this;
	}
	@Field(6) 
	public short wSecond() {
		return this.io.getShortField(this, 6);
	}
	@Field(6) 
	public SYSTEMTIME wSecond(short wSecond) {
		this.io.setShortField(this, 6, wSecond);
		return this;
	}
	@Field(7) 
	public short wMilliseconds() {
		return this.io.getShortField(this, 7);
	}
	@Field(7) 
	public SYSTEMTIME wMilliseconds(short wMilliseconds) {
		this.io.setShortField(this, 7, wMilliseconds);
		return this;
	}
	public SYSTEMTIME() {
		super();
	}
	public SYSTEMTIME(Pointer pointer) {
		super(pointer);
	}
}
