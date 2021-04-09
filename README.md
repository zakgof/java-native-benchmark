# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [Project Panama](http://openjdk.java.net/projects/panama/) Foreign Memory Access and Foreign Linker APIs.

Updated April 9, 2021

## Benchmark operation ##
Get seconds from the current system time using native call to Windows API function `GetSystemTime` provided by kernel32.dll:

````cpp
void GetSystemTime(LPSYSTEMTIME lpSystemTime);
````
with the data structure defined as
````cpp
typedef struct _SYSTEMTIME {
  WORD wYear;
  WORD wMonth;
  WORD wDayOfWeek;
  WORD wDay;
  WORD wHour;
  WORD wMinute;
  WORD wSecond;
  WORD wMilliseconds;
} SYSTEMTIME, *PSYSTEMTIME, *LPSYSTEMTIME;
````
Each implementation will
1. allocate memory for the `SYSTEMTIME` struct
2. call native method `GetSystemTime` passing the allocated memory
3. extract and return the value from the field `wSecond`

In a separate benchmark I measured performance of the native call only  (item 2).

**JNI**     
JNI is a Java's standard way to call native code present in JDK since its early versions. JNI requires building a native stub as an adapter between Java and native library, so is considered low-level. Helper tools have been developed in order to automate and simplify native stub generation. Here I used [JavaCpp](https://github.com/bytedeco/javacpp), the project is known for prebaking Java wrappers around high-performant C/C++ libraries such as OpenCV and ffmpeg.
JavaCpp comes with ready-to-use wrappers for widely used system libraries, including Windows API lib, so I used them in this benchmark.

**JNA**     
JNA resolves the burden of writing native wrapper by using a native stub that calls the target function dynamically. It only requires writing Java code and provides mapping to C structs and unions, however, for complex libraries writing Java API that matched a native lib's C API still might be a big task. JNA also provides prebaked Java classes for Windows API. Wrapping the calls dynamically results in high performance overhead comparing to JNI.

**JNA Direct**    
JNA's direct mode claims to "improve performance substantially, approaching that of custom JNI". That should be well seen then calls are using mostly primitive types for arguments and return values.   

**BriJ**     
Bridj is an attempt to provide a Java to Cpp interop solution similar to JNA (without a need of writing and compiling native code), it claims to provide better performance using dyncall and hand-optimized assembly tweaks. A tool named JNAerator helps to generate java classed from the native library headers. The Bridj projects seems to be abandoned now.

**JNR**     
JNR is a comparingly young project that target the same problem. Similarly as JNA or Bridj it does not require native programming. There's not much documentation or reviews at the moment, but JNR is often called promising.

**Project Panama**     
Project Panama aims to simplify the existing complexity with Java to C interop on JDK level. It is still under development, but Foreign Memory Access and Foreign Linker APIs are already available is openjdk 16.

**Pure Java**    
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

## How to run ##

Make sure that gradle is configured with a JDK 16 (or later) and run
````
gradlew clean jmh
````

## Results ##

**System**:  

Intel Core i5-6500 @ 3.20 GHz / Windows 10 / openjdk-14-panama+1-15

```
Full benchmark (average time, smaller is better)

JmhGetSystemTimeSeconds.jnaDirect           2962.544 ± 191.795  ns/op
JmhGetSystemTimeSeconds.jna                 2889.632 ± 173.064  ns/op
JmhGetSystemTimeSeconds.bridj                937.159 ±  59.353  ns/op
JmhGetSystemTimeSeconds.jnr                  362.979 ±   3.560  ns/op
JmhGetSystemTimeSeconds.panama               242.100 ±   2.240  ns/op
JmhGetSystemTimeSeconds.jni_javacpp          216.767 ±   2.239  ns/op
JmhGetSystemTimeSeconds.java_calendar        173.949 ±   3.707  ns/op
JmhGetSystemTimeSeconds.java_localdatetime    70.926 ±   0.670  ns/op
JmhGetSystemTimeSeconds.java_date             63.818 ±   2.434  ns/op
```

JNA looks expectedly slow (x13 slower that JNI). JNA direct appears even slower, as probably mapping the struct from C to Java consumes the most of operation's time.

Trending JNR appears faster than outdated Bridj, yet staying behind JNI.

Panama APIs demonstrate performance comparable to that of JNI. This looks promising as Oracle's further development of Project Panama is based on these APIs. 

JNI itself is still noticeably slower than pure Java. Note that the fastest API was `java.util.Date` (with a deprecated but still working `Date.getSeconds`). The JDK8's `LocalDateTime` is ~2.4 times faster than Calendar API, but yet a little slower than the old-style `j.u.Date`.


Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only (average time, smaller is better)

JmhCallOnly.jna                             1074.267 ±   8.909  ns/op
JmhCallOnly.jna_direct                      1146.169 ±  23.575  ns/op
JmhCallOnly.bridj                            307.207 ±   6.025  ns/op
JmhCallOnly.jnr                              256.508 ±   3.558  ns/op
JmhCallOnly.jni_javacpp                       44.727 ±   0.255  ns/op
JmhCallOnly.panama                            44.323 ±   0.709  ns/op
````
The order is nearly the same, leaving JNI and Panama the fastest.
