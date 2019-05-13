# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi), [Bridj](https://github.com/nativelibs4java/BridJ) and [Project Panama](http://openjdk.java.net/projects/panama/) (jdk13 ea)

**Operation:** 
Get seconds from the current system time using native call to Windows API function `GetSystemTime` provided by kernel32.dll.

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
JavaCpp comes with ready-to-use wrappers for widely used system libraries, including Windows API lib. In this benchmark I used both prebaked Windows API (jni_jcpp_stock) as well as the customly generated wrapper lib for the single `GetSystemTime` method (jni_jcpp_custom).

**JNA**     
JNA resolves the burden of writing native wrapper by using a native stub that calls the target function dynamically. It only requires writing Java code and provides mapping to C structs and unions, however, for complex libraries writing Java API that matched a native lib's C API still might be a big task. JNA also provides prebaked Java classes for Windows API. Wrapping the calls dynamically results in high performance overhead comparing to JNI.

**BriJ**     
Bridj is an attempt to provide a Java to Cpp interop solution similar to JNA (without a need of writing and compiling native code), it claims to provide better performance using dyncall and hand-optimized assembly tweaks. A tool named JNAerator helps to generate java classed from the native library headers. The Bridj projects seems to be abandoned now.

**JNR**     
JNR is a comparingly young project that resolves the same problems, same as JNA or Bridj it does not require native programming. There's not much documentation or reviews at the moment, but JNR is often called promising.

**Project Panama**     
Project Panama aims to simplify the existing complexity with Java to C interop on JDK level. It is still under development, but already available is some jdk 13 early access builds. Panama developers claim to provide high performance and ease of use.
I noticed that the `straightforward` calling of the wrapped API involved scanning the struct layout on each step. An optimized code was writted to calculate layout once and reuse it in subsequents calls (panama_prelayout)

**Pure Java**
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

**System**:  

Intel Core i5-6500 @ 3.20 GHz / Windows 10 / openjdk-13-panama-f70

## Results ###
```
Full benchmark

JmhGetSystemTimeSeconds.panama              7993.787 ± 3161.884  ns/op
JmhGetSystemTimeSeconds.jna                 3917.292 ±  874.940  ns/op
JmhGetSystemTimeSeconds.panama_prelayout    1244.939 ±   53.293  ns/op
JmhGetSystemTimeSeconds.jni                  781.525 ±   97.425  ns/op
JmhGetSystemTimeSeconds.jni_minimal          770.816 ±   51.910  ns/op
JmhGetSystemTimeSeconds.bridj                745.879 ±   87.116  ns/op
JmhGetSystemTimeSeconds.jnr                  418.712 ±   33.270  ns/op
JmhGetSystemTimeSeconds.java_calendar        186.321 ±    6.481  ns/op
JmhGetSystemTimeSeconds.java_localdatetime    77.483 ±    5.103  ns/op
JmhGetSystemTimeSeconds.java_date             62.758 ±    2.037  ns/op
```

JNA looks expectedly slow (x5 slower that JNI).   
It may look strange that Bridj build on top of JNI could outperform JNI, but remember that the benchmark involves not only calling a native method but also allocating a struct and extracting the result from a field, we'll see the native-call-only results below.

JNI tests show similar results (within error range) with custom and stock wrapper. It is still much slower than pure Java. Note that the fastest API was `java.util.Date` (with a deprecated but still working `Date.getSeconds`). The JDK8's `LocalDateTime` is ~2.4 times faster than Calendar API, but yet a little slower than the old-style `j.u.Date`.

I was surprised by the slowliness of Panama. Even with optimized layout scanning Panama is much slower that JNI.

Now let's look into performance of the native call only, stripping out the struct allocation and field access:

````
Native call only

JmhCallOnly.jna                             1183.508 ±   21.163  ns/op
JmhCallOnly.panama                           616.936 ±    6.047  ns/op
JmhCallOnly.bridj                            300.386 ±    3.019  ns/op
JmhCallOnly.jnr                              273.134 ±    3.303  ns/op
JmhCallOnly.jni_jcpp_custom                   72.369 ±    0.574  ns/op
JmhCallOnly.jni_jcpp_stock                    73.818 ±    0.552  ns/op
````

Here JNI significanlty outperforms all the competitors with custom wrapper a bit faster than the JavaCpp-prebaked windows API.

Trending JNR appears faster than outdated Bridj, with Panama trailing behind and JNA being the slowest.

Poor results of JNI full operation benchmark can be explained by the slowliness of struct allocation or the files access (which is actually JavaCpp's area of responsibility). I am wondering if it's possible to use Bridj's approach for fast allocation with pure JNI...

#Conclusion

In 2019, getting the ultimate performance from Java to C++ interop still requires JNI with its low-level routine work. For a more developer-friendly solution JNR should be recommended. I hope that Panama should be able to gain some speed before its release, for now its performance is disappointing.
