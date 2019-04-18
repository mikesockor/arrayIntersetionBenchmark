# java sorted array intersetion JMH benchmark

benchmark mode is Throughput and in this case higher value of score is better.

```
Benchmark                        Mode  Cnt   Score    Error   Units
Intersect.intersectWithHash     thrpt    5   0.001 ±  0.001  ops/ns
Intersect.intersectWithLoop1    thrpt    5   0.006 ±  0.001  ops/ns
Intersect.intersectWithLoop2    thrpt    5   0.006 ±  0.003  ops/ns
Intersect.intersectWithStream1  thrpt    5  ≈ 10⁻⁴           ops/ns
Intersect.intersectWithStream2  thrpt    5   0.002 ±  0.001  ops/ns
```
