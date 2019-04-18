package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
public class IntersectSmallSize {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        public int[] ar1;
        public int[] ar2;

        @Setup(Level.Invocation)
        public void setUp() {
            ar1 = new int[] { 1, 2, 3, 3, 7, 8, 11, 20, 23, 34, 67 };
            ar2 = new int[] { 0, 3, 11, 23, 23, 56, 67, 78, 90, 100, 100, 123, 145, 156, 178, 190 };
        }
    }

    /**
     * IntersectLargeSize with loop plain.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoop1(final ExecutionPlan plan) {

        List<Integer> result = new ArrayList<>();
        for (int value : plan.ar1) {
            for (int i : plan.ar2) {
                if (i == value) {
                    result.add(value);
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
    public void intersectWithLoop2(final ExecutionPlan plan) {

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

    /**
     * IntersectLargeSize with hash.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithHash(final ExecutionPlan plan) {

        Set<Integer> s1 = Arrays.stream(plan.ar1).boxed().collect(Collectors.toSet());
        Set<Integer> s2 = Arrays.stream(plan.ar2).boxed().collect(Collectors.toSet());
        s1.retainAll(s2);
    }

    /**
     * IntersectLargeSize with stream compare with stream any match.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithStream1(final ExecutionPlan plan) {

        final Supplier<IntStream> ss2 = () -> Arrays.stream(plan.ar2);
        Arrays.stream(plan.ar1)
            .distinct()
            .filter(x -> ss2.get().anyMatch(y -> y == x))
            .toArray();
    }

    /**
     * IntersectLargeSize with stream compare with list contains.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithStream2(final ExecutionPlan plan) {

        final List<Integer> l2 = Arrays.stream(plan.ar2).boxed().collect(Collectors.toList());
        Arrays.stream(plan.ar1)
            .distinct()
            .filter(l2::contains)
            .toArray();
    }

}
