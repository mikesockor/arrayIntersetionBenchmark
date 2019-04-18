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
public class IntersectLargeSize {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        public int[] ar1;
        public int[] ar2;

        @Setup(Level.Invocation)
        public void setUp() {
            Supplier<IntStream> intStream = () -> IntStream.generate(() -> (int) (Math.random()));
            ar1 = intStream.get().limit(3000000).sorted().toArray();
            ar2 = intStream.get().limit(5000000).sorted().toArray();
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
