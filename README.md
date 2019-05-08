# java-native-benchmark
JMH performance benchmark for Java's native call APIs: [JNI](https://docs.oracle.com/en/java/javase/12/docs/specs/jni/index.html) (via [JavaCpp](https://github.com/bytedeco/javacpp) ), [JNA](https://github.com/java-native-access/jna), [JNR](https://github.com/jnr/jnr-ffi) and [Bridj](https://github.com/nativelibs4java/BridJ).

**Operation:** get seconds from the current time using native call to Windows API function `GetSystemTime` provided by kernel32.dll.
For comparison, the same problem was implemented with JDK's `java.util.Date`, `java.util.Calendar` and `java.time.LocalDateTime`

**System**:  Intel Core i5-6500 @ 3.20 GHz / Windows 10 / openjdk-12-ga

## results ###

```
  Benchmark         |     Score     |   Error     | Units |
-----------------------------------------------------------
java_date           |  16729936.223 |  94479.468  | ops/s |
java_localdatetime  |  13559181.772 | 174049.796  | ops/s |
java_calendar       |   6399013.103 |  48930.592  | ops/s |
jnr                 |   2645225.092 |  14146.879  | ops/s |
bridj               |   1108722.699 |  86766.727  | ops/s |
jni                 |   1072173.526 | 110808.691  | ops/s |
jna                 |    357043.918 |  20771.694  | ops/s |
```
