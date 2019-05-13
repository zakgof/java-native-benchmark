package com.zakgof.jnbenchmark.jni.gen;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Platform;

@Platform
public class NativeLibraryConfig {

	public static class SYSTEMTIME extends Pointer {
        static { Loader.load(); }
        /** Default native constructor. */
        public SYSTEMTIME() { super((Pointer)null); allocate(); }
        /** Native array allocator. Access with {@link Pointer#position(long)}. */
        public SYSTEMTIME(long size) { super((Pointer)null); allocateArray(size); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public SYSTEMTIME(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(long size);
        @SuppressWarnings("unchecked")
		@Override public SYSTEMTIME position(long position) {
            return (SYSTEMTIME)super.position(position);
        }

        public native @Cast("WORD") short wYear(); public native SYSTEMTIME wYear(short wYear);
        public native @Cast("WORD") short wMonth(); public native SYSTEMTIME wMonth(short wMonth);
        public native @Cast("WORD") short wDayOfWeek(); public native SYSTEMTIME wDayOfWeek(short wDayOfWeek);
        public native @Cast("WORD") short wDay(); public native SYSTEMTIME wDay(short wDay);
        public native @Cast("WORD") short wHour(); public native SYSTEMTIME wHour(short wHour);
        public native @Cast("WORD") short wMinute(); public native SYSTEMTIME wMinute(short wMinute);
        public native @Cast("WORD") short wSecond(); public native SYSTEMTIME wSecond(short wSecond);
        public native @Cast("WORD") short wMilliseconds(); public native SYSTEMTIME wMilliseconds(short wMilliseconds);
    }

}
