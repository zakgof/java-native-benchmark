# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [Project Panama](http://openjdk.java.net/projects/panama/) (Early access builds openjdk-13-foreign+70 and openjdk-14-panama+1-15)

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
Project Panama aims to simplify the existing complexity with Java to C interop on JDK level. It is still under development, but already available is openjdk early access builds. Panama developers claim to provide high performance and ease of use.
I noticed that the _straightforward_ calling of the wrapped API involved scanning the struct layout on each step. An optimized code was writted to calculate layout once and reuse it in subsequents calls (panama_prelayout)

**Pure Java**    
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

## How to run ##

Make sure that gradle is configured with a JDK including Panama implementation, such as https://download.java.net/java/early_access/panama/1/openjdk-14-panama+1-15_windows-x64_bin.zip
````
    gradlew clean jmh
````

## Results ##

**System**:  

Intel Core i5-6500 @ 3.20 GHz / Windows 10 / openjdk-14-panama+1-15

```
Full benchmark (average time, smaller is better)

JmhGetSystemTimeSeconds.panama               3975.265 ± 123.124  ns/op
JmhGetSystemTimeSeconds.jnaDirect            3209.387 ± 292.006  ns/op
JmhGetSystemTimeSeconds.jna                  3067.265 ± 292.253  ns/op
JmhGetSystemTimeSeconds.bridj                1007.170 ± 236.825  ns/op
JmhGetSystemTimeSeconds.panama_prelayout      691.253 ±   5.381  ns/op
JmhGetSystemTimeSeconds.jnr                   364.128 ±   4.929  ns/op
JmhGetSystemTimeSeconds.jni_javacpp           213.927 ±   4.199  ns/op
JmhGetSystemTimeSeconds.java_calendar         165.560 ±   1.601  ns/op
JmhGetSystemTimeSeconds.java_date              62.951 ±   1.986  ns/op
JmhGetSystemTimeSeconds.java_localdatetime     73.072 ±   1.080  ns/op
```

JNA looks expectedly slow (x5 slower that JNI). JNA direct appears even slower, as probably mapping the struct from C to Java consumes the most of operation's time.

Panama is not as good as it might be expected. Used _as is_, it's the slowest in this benchmark. Even with optimized layout scanning Panama is still 3 times slower than JNI.

Trending JNR appears faster than outdated Bridj, yet staying behind JNI.

JNI itself is still ~4 times slower than pure Java. Note that the fastest API was `java.util.Date` (with a deprecated but still working `Date.getSeconds`). The JDK8's `LocalDateTime` is ~2.4 times faster than Calendar API, but yet a little slower than the old-style `j.u.Date`.


Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only (average time, smaller is better)

JmhCallOnly.jna_direct                       1179.017 ±  28.721  ns/op
JmhCallOnly.jna                              1107.120 ±  14.233  ns/op
JmhCallOnly.panama                            636.456 ±   9.554  ns/op
JmhCallOnly.bridj                             292.250 ±   4.798  ns/op
JmhCallOnly.jnr                               247.982 ±   3.127  ns/op
JmhCallOnly.jni_javacpp                        36.568 ±   0.327  ns/op
````

The order is nearly the same, leaving JNI the best with JNR as a leader among native-code-development-free solutions.

## Panama early access build evolution ##

It's interesting to compare the performance of the two publicly available Panama early access builds:

```
openjdk-13-foreign+70  - March 2019 
openjdk-14-panama+1-15 - August 2019
```

The latter mentions _Panama runtime performance enhancements_ in the release notes.

Let's see how the figures changed for this particular benchmark:

```
JmhGetSystemTimeSeconds.panama / March 2019               4242.718 ± 156.266  ns/op
JmhGetSystemTimeSeconds.panama / August 2019              3975.265 ± 123.124  ns/op  (6% faster)

JmhGetSystemTimeSeconds.panama_prelayout / March 2019     1048.450 ±  11.403  ns/op
JmhGetSystemTimeSeconds.panama_prelayout / August 2019     691.253 ±   5.381  ns/op  (34% faster)

JmhCallOnly.panama / March  2019                           566.609 ±   6.338  ns/op
JmhCallOnly.panama / August 2019                           636.456 ±   9.554  ns/op  (12% slower)
```

Looks like the recent Panama optimizations addressed struct allocation and field extraction, resulting in a significant speed gain for the overall test, but the actual native call performance did not improve. Keep waiting for the public release.


## Conclusion ##

In 2019, getting the ultimate performance from Java to C++ interop still requires JNI with its low-level routine work. For a more developer-friendly solution JNR should be recommended. I still hope that Panama is able to gain some speed before its release, but for now its performance is disappointing.
