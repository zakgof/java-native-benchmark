# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [Project Panama](http://openjdk.java.net/projects/panama/) (jdk13 ea)

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
JNA's direct mode claims to "improve performance substantially, approaching that of custom JNI". The should be well seen then calls are using mostly primitive types for arguments and return values.   

**BriJ**     
Bridj is an attempt to provide a Java to Cpp interop solution similar to JNA (without a need of writing and compiling native code), it claims to provide better performance using dyncall and hand-optimized assembly tweaks. A tool named JNAerator helps to generate java classed from the native library headers. The Bridj projects seems to be abandoned now.

**JNR**     
JNR is a comparingly young project that target the same problem. Similarly as JNA or Bridj it does not require native programming. There's not much documentation or reviews at the moment, but JNR is often called promising.

**Project Panama**     
Project Panama aims to simplify the existing complexity with Java to C interop on JDK level. It is still under development, but already available is some jdk 13 early access builds. Panama developers claim to provide high performance and ease of use.
I noticed that the `straightforward` calling of the wrapped API involved scanning the struct layout on each step. An optimized code was writted to calculate layout once and reuse it in subsequents calls (panama_prelayout)

**Pure Java**
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

## How to run ##

Make sure that gradle is configured with a JDK including Panama implementation, such as https://download.java.net/java/early_access/panama/70/openjdk-13-foreign+70_windows-x64_bin.zip
````
    gradlew clean jmh
````

## Results ##

**System**:  

Intel Core i5-6500 @ 3.20 GHz / Windows 10 / openjdk-13-panama-f70

```
Full benchmark

JmhGetSystemTimeSeconds.panama              3688.485 ±  75.169  ns/op
JmhGetSystemTimeSeconds.jnaDirect           2778.373 ± 287.277  ns/op
JmhGetSystemTimeSeconds.jna                 2723.679 ± 399.708  ns/op
JmhGetSystemTimeSeconds.panama_prelayout     875.691 ±  20.309  ns/op
JmhGetSystemTimeSeconds.bridj                756.305 ±  81.670  ns/op
JmhGetSystemTimeSeconds.jnr                  309.383 ±   6.621  ns/op
JmhGetSystemTimeSeconds.jni_javacpp          227.560 ±   3.355  ns/op
JmhGetSystemTimeSeconds.java_calendar        136.523 ±   0.947  ns/op
JmhGetSystemTimeSeconds.java_localdatetime    59.366 ±   0.461  ns/op
JmhGetSystemTimeSeconds.java_date             45.889 ±   0.396  ns/op
```

JNA looks expectedly slow (x5 slower that JNI).

I was surprised by the slowliness of Panama. Even with optimized layout scanning Panama is 4 times slower than JNI, beating only JNA.       

Trending JNR appears faster than outdated Bridj, yet staying behind JNI.

JNI itself is still ~4 times slower than pure Java. Note that the fastest API was `java.util.Date` (with a deprecated but still working `Date.getSeconds`). The JDK8's `LocalDateTime` is ~2.4 times faster than Calendar API, but yet a little slower than the old-style `j.u.Date`.



Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only

JmhCallOnly.jna_direct                       898.687 ±   7.515  ns/op
JmhCallOnly.jna                              885.746 ±   8.698  ns/op
JmhCallOnly.panama                           469.585 ±   4.441  ns/op
JmhCallOnly.bridj                            231.785 ±   2.663  ns/op
JmhCallOnly.jnr                              214.501 ±  11.218  ns/op
JmhCallOnly.jni_javacpp                       56.872 ±   0.845  ns/op
````

The order of the same, leaving JNI the best with JNR as a leader among native-code-development-free solutions.

## Conclusion ##

In 2019, getting the ultimate performance from Java to C++ interop still requires JNI with its low-level routine work. For a more developer-friendly solution JNR should be recommended. I still hope that Panama is able to gain some speed before its release, but for now its performance is disappointing.
