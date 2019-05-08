package com.zakgof.jnbenchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

import com.zakgof.jnbenchmark.bridj.BridjBenchmark;
import com.zakgof.jnbenchmark.java.JustJava;
import com.zakgof.jnbenchmark.jna.JnaBenchmark;
import com.zakgof.jnbenchmark.jni.JniBenchmark;
import com.zakgof.jnbenchmark.jnr.JnrBenchmark;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class JmhGetSystemTimeSeconds {

	@Benchmark
	public void java_localdatetime() throws InterruptedException {
		JustJava.java_ldt();
	}

	@Benchmark
	public void java_calendar() throws InterruptedException {
		JustJava.java_calendar();
	}
	
	@Benchmark
	public void java_date() throws InterruptedException {
		JustJava.java_date();
	}

	@Benchmark
	public void jni() throws InterruptedException {
		JniBenchmark.run();
	}

	@Benchmark
	public void jna() throws InterruptedException {
		JnaBenchmark.jna();
	}

	@Benchmark
	public void jnr() throws InterruptedException {
		JnrBenchmark.run();
	}

	@Benchmark
	public void bridj() throws InterruptedException {
		BridjBenchmark.run();
	}

}
