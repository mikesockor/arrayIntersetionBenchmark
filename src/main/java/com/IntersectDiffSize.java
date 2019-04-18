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
            ar1 = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 };
            ar2 = new int[] { 22, 23, 24, 25, 26, 27, 28, 29, 30 };
        }
    }

    /**
     * IntersectLargeSize with loop with starting point memoization and size control.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoopSizeControl(final ExecutionPlan plan) {

        // to define outer array with less length
        int[] lr1;
        int[] lr2;
        if (plan.ar1.length < plan.ar2.length) {
            lr1 = plan.ar1;
            lr2 = plan.ar2;
        }
        else {
            lr1 = plan.ar2;
            lr2 = plan.ar1;
        }

        // to stop iterate outer array if last value in inner is bigger
        int lastInnerValue = lr2[lr2.length - 1];

        List<Integer> result = new ArrayList<>();

        // to start iterate inner array from last position
        int startingPoint = 0;
        for (int value : lr1) {

            if (value > lastInnerValue)
                break;

            for (int i = startingPoint; i < lr2.length; i++) {

                if (lr2[i] > value)
                    break;

                if (lr2[i] == value) {
                    result.add(value);
                    startingPoint = i;
                    break;
                }
            }
        }
    }

}
