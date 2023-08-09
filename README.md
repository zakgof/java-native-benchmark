# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [JDK JEP-424](https://openjdk.org/jeps/424) Foreign Function/Memory APIs (Preview).

Updated **August 9, 2023**

See historical results here: [August 2019](https://github.com/zakgof/java-native-benchmark/tree/August-2019#readme)

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

**JDK Foreign Function/Memory API Preview (JEP-424)**
API by which Java programs can interoperate with code and data outside of the Java runtime.

**Pure Java**    
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

## How to run ##

Make sure that gradle is configured with a JDK 19 and run
````
gradlew clean jmh
````

## Results ##

**System**:  

Intel Core i7-10610U CPU @ 1.80GHz / Windows 10 / openjdk-19.0.1
```
Full benchmark (average time, smaller is better)

JmhGetSystemTimeSeconds.jnaDirect            4517.766 ± 417.656  ns/op
JmhGetSystemTimeSeconds.jna                  4037.103 ± 681.270  ns/op
JmhGetSystemTimeSeconds.bridj                1087.531 ± 122.028  ns/op
JmhGetSystemTimeSeconds.jnr                   400.896 ±  52.783  ns/op
JmhGetSystemTimeSeconds.jni_javacpp           259.521 ±   7.964  ns/op
JmhGetSystemTimeSeconds.foreign               237.920 ±  30.081  ns/op
JmhGetSystemTimeSeconds.java_calendar         154.341 ±   8.306  ns/op
JmhGetSystemTimeSeconds.java_localdatetime     85.310 ±  32.671  ns/op
JmhGetSystemTimeSeconds.java_date              58.209 ±   3.257  ns/op

```

JNA looks expectedly slow (x13 slower that JNI). JNA direct appears even slower, as probably mapping the struct from C to Java consumes the most of operation's time.

JNR appears faster than outdated Bridj, yet staying behind JNI.

JDK's foreign APIs demonstrate performance twice faster than JNI. This looks much better than 2019 results confirming that the significant performance optimization tool place within JDKs 15-19. 

Foreign APIs itself are still a little slower than pure Java. Note that the fastest API was `java.util.Date` (with a deprecated but still working `Date.getSeconds`). The JDK8+'s `LocalDateTime` is ~2.4 times faster than Calendar API, but yet a little slower than the old-style `j.u.Date`.


Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only (average time, smaller is better)

JmhCallOnly.jna_direct                       1373.435 ±  70.343  ns/op
JmhCallOnly.jna                              1346.036 ±  72.239  ns/op
JmhCallOnly.bridj                             383.992 ±  50.000  ns/op
JmhCallOnly.jnr                               298.334 ±  48.785  ns/op
JmhCallOnly.jni_javacpp                        56.605 ±   8.087  ns/op
JmhCallOnly.foreign                            49.717 ±   6.667  ns/op
````
The order is nearly the same, and Panama is a leader.
