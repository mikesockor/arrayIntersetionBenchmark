package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
@BenchmarkMode({ Mode.SampleTime })
//@BenchmarkMode({ Mode.SampleTime, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Intersect {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        public Integer[] ar1;
        public Integer[] ar2;

        @Setup(Level.Invocation)
        public void setUp() {
            ar1 = new Integer[] { 1, 2, 3, 3, 7, 8, 11, 20, 23, 34, 67 };
            ar2 = new Integer[] { 0, 3, 11, 23, 23, 56, 67, 78, 90, 100, 100, 123, 145, 156, 178, 190 };
        }
    }

    /**
     * Intersect with loop plain.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoop1(ExecutionPlan plan) {

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
     * Intersect with loop with starting point memoization.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithLoop2(ExecutionPlan plan) {

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
     * Intersect with hash.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithHash(ExecutionPlan plan) {

        Set<Integer> s1 = Arrays.stream(plan.ar1).collect(Collectors.toSet());
        Set<Integer> s2 = Arrays.stream(plan.ar2).collect(Collectors.toSet());
        s1.retainAll(s2);
    }

    /**
     * Intersect with stream compare with stream any match.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithStream1(ExecutionPlan plan) {

        Supplier<Stream<Integer>> ss2 = () -> Stream.of(plan.ar2).distinct();
        Arrays.stream(plan.ar1)
            .distinct()
            .filter(x -> ss2.get().anyMatch(y -> y.equals(x)))
            .toArray();
    }

    /**
     * Intersect with stream compare with list contains.
     *
     * @param plan the plan
     */
    @Benchmark
    public void intersectWithStream2(ExecutionPlan plan) {

        List<Integer> l2 = Arrays.asList(plan.ar2);
        Arrays.stream(plan.ar1)
            .distinct()
            .filter(l2::contains)
            .toArray();
    }

}
