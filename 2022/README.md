# My Advent of Code 2022 solutions

See the root [README.md](../README.md) on general repository information.

## Day 1

A very nice warmup puzzle. The [functional implementation](src/main/kotlin/day1_func.kt) is pretty idiomatic Kotlin. The
[imperative](src/main/kotlin/day1_imp.kt) variant is also very similar, just opting to collect sums into an array and
sorting it in place.

[Fast version](src/main/kotlin/day1_fast.kt) was fun to write, ended up iterating over the input once and using 3 local variables to
implement a tiny tree where the root node is always the smallest value, giving me a very fast check
whether a sum should be considered or not.

Performance-wise imperative and functional ended up being roughly the same whereas fast was around ~11x
quicker:

```text
Benchmark                 Mode  Cnt        Score      Error  Units
Day1.benchmarkFastPart2  thrpt    9  1122763.661 ± 9806.305  ops/s
Day1.benchmarkFuncPart2  thrpt    9   102806.703 ± 3846.851  ops/s
Day1.benchmarkImpPart2   thrpt    9   101182.700 ± 4573.944  ops/s
```
