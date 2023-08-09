package com.zakgof.jnbenchmark.foreign;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.MemoryLayout.structLayout;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_SHORT;

public class JdkForeignBenchmark {

    private static final MemoryLayout SYSTEMTIME = structLayout(
            JAVA_SHORT.withName("wMonth"),
            JAVA_SHORT.withName("wDayOfWeek"),
            JAVA_SHORT.withName("wDay"),
            JAVA_SHORT.withName("wHour"),
            JAVA_SHORT.withName("wMinute"),
            JAVA_SHORT.withName("wSecond"),
            JAVA_SHORT.withName("wMilliseconds")
    );

    private final VarHandle wSecondHandle;
    private final MethodHandle mhGetSystemTime;
    private final MemorySegment globalSystemTime;
    private final MemorySession globalMemorySession;


    public JdkForeignBenchmark() {

        System.loadLibrary("Kernel32");
        SymbolLookup lookup = SymbolLookup.loaderLookup();
        Linker linker = Linker.nativeLinker();
        MemorySegment getSystemTime = lookup.lookup("GetSystemTime").orElseThrow();

        this.wSecondHandle = SYSTEMTIME.varHandle(MemoryLayout.PathElement.groupElement("wSecond"));


        this.mhGetSystemTime = linker.downcallHandle(getSystemTime, FunctionDescriptor.ofVoid(ADDRESS));

        globalMemorySession = MemorySession.global();
        globalSystemTime = globalMemorySession.allocate(SYSTEMTIME);
    }

    public short all() {
        try {
            MemorySegment lpSystemTime = globalMemorySession.allocate(SYSTEMTIME);
            mhGetSystemTime.invoke(lpSystemTime);
            return (Short) wSecondHandle.get(lpSystemTime);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void callOnly() {
        try {
            mhGetSystemTime.invoke(globalSystemTime);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
