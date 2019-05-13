package com.zakgof.jnbenchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.zakgof.jnbenchmark.bridj.BridjBenchmark;
import com.zakgof.jnbenchmark.jna.JnaBenchmark;
import com.zakgof.jnbenchmark.jni.JavaCppStock;
import com.zakgof.jnbenchmark.jnr.JnrBenchmark;
import com.zakgof.jnbenchmark.panama.PanamaBenchmark;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JmhCallOnly {

	private PanamaBenchmark panama;
	private JavaCppStock javacppjni;
	private BridjBenchmark bridj;
	private JnaBenchmark jna;
	private JnrBenchmark jnr;

	@Setup(Level.Trial)
	public void setup() {
		panama = new PanamaBenchmark();
		javacppjni = new JavaCppStock();
		bridj = new BridjBenchmark();
		jna = new JnaBenchmark();
		jnr = new JnrBenchmark();
	}

	@Benchmark
	public void jni_javacpp() throws InterruptedException {
		javacppjni.callOnly();
	}

	@Benchmark
	public void panama() throws InterruptedException {
		panama.callOnly();
	}

	@Benchmark
	public void bridj() throws InterruptedException {
		bridj.callOnly();
	}

	@Benchmark
	public void jna() throws InterruptedException {
		jna.callOnly();
	}

	@Benchmark
	public void jnr() throws InterruptedException {
		jnr.callOnly();
	}

}
