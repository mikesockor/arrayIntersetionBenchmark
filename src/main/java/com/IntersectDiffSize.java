package com;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author S750976
 */
@Fork(value = 1, warmups = 1)
@BenchmarkMode({ Mode.Throughput })
//@BenchmarkMode({ Mode.SampleTime, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IntersectDiffSize {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        public int[] ar1;
        public int[] ar2;

        @Setup(Level.Invocation)
        public void setUp() {
            ar1 = new int[] { 22, 23, 24, 25, 26 };
            ar2 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
        }
    }

    /**
     * IntersectLargeSize with loop with starting point memoization.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoopOriginal(final ExecutionPlan plan) {

        List<Integer> result = new ArrayList<>();
        int startingPoint = 0;
        for (int value : plan.ar1) {
            for (int i = startingPoint; i < plan.ar2.length; i++) {
                if (plan.ar2[i] > value)
                    break;
                if (plan.ar2[i] == value) {
                    result.add(value);
                    startingPoint = i;
                    break;
                }
            }
        }
    }

    /**
     * IntersectLargeSize with loop with starting point memoization.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoopReverted(final ExecutionPlan plan) {

        List<Integer> result = new ArrayList<>();
        int startingPoint = 0;
        for (int value : plan.ar2) {
            for (int i = startingPoint; i < plan.ar1.length; i++) {
                if (plan.ar1[i] > value)
                    break;
                if (plan.ar1[i] == value) {
                    result.add(value);
                    startingPoint = i;
                    break;
                }
            }
        }
    }

}
