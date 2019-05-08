package com.zakgof.jnbenchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 300, timeUnit = TimeUnit.MILLISECONDS)
public class JmhGetSystemTimeSeconds {
	
	@Benchmark
	public void java_ldt() throws InterruptedException {
		Main.java_ldt();
	}
	
	@Benchmark
	public void java_calendar() throws InterruptedException {
		Main.java_calendar();
	}
	
	@Benchmark
	public void jni() throws InterruptedException {
		Main.jni_javacpp();
	}
	
	@Benchmark
	public void jna() throws InterruptedException {
		Main.jna();
	}
	
	@Benchmark
	public void jnr() throws InterruptedException {
		Jnr.run();
	}

}
