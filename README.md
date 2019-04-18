# java sorted array intersetion JMH benchmark

benchmark mode is Throughput and in this case higher value of score is better.

### small example
ar1 = new int[] { 1, 2, 3, 3, 7, 8, 11, 20, 23, 34, 67 }; </p>
ar2 = new int[] { 0, 3, 11, 23, 23, 56, 67, 78, 90, 100, 100, 123, 145, 156, 178, 190 };

```
Benchmark                        Mode  Cnt   Score    Error   Units
Intersect.intersectWithHash     thrpt    5   0.001 ±  0.001  ops/ns
Intersect.intersectWithLoop1    thrpt    5   0.006 ±  0.001  ops/ns
Intersect.intersectWithLoop2    thrpt    5   0.006 ±  0.003  ops/ns
Intersect.intersectWithStream1  thrpt    5  ≈ 10⁻⁴           ops/ns
Intersect.intersectWithStream2  thrpt    5   0.002 ±  0.001  ops/ns
```

### quite large example
ar1 = intStream.limit(3000000).sorted().toArray();</p>
ar2 = intStream.limit(5000000).sorted().toArray();

```
Benchmark                                 Mode  Cnt   Score    Error   Units
IntersectLargeSize.intersectWithHash     thrpt    5  ≈ 10⁻⁸           ops/ns
IntersectLargeSize.intersectWithLoop1    thrpt    5  ≈ 10⁻⁷           ops/ns
IntersectLargeSize.intersectWithLoop2    thrpt    5  ≈ 10⁻⁷           ops/ns
IntersectLargeSize.intersectWithStream1  thrpt    5  ≈ 10⁻⁷           ops/ns
IntersectLargeSize.intersectWithStream2  thrpt    5  ≈ 10⁻⁸           ops/ns
```