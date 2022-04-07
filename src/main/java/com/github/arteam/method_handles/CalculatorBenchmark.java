package com.github.arteam.method_handles;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CalculatorBenchmark {

    private long number = 617214092523509L;
    private Calculator calculator = new Calculator(true, false, true, false);

    //@Benchmark
    public long test1() {
        return calculator.calculate1(number);
    }

    // @Benchmark
    public long test2() {
        return calculator.calculate2(number);
    }

    @Benchmark
    public long test3() {
        return calculator.calculate3(number);
    }

    //@Benchmark
    public long test4() {
        return calculator.calculate3(number);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(CalculatorBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
