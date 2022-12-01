package bench

import Day1Fast
import Day1Func
import Day1Imp
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import utils.readFile

@State(Scope.Benchmark)
open class Day1 {
  private val inputString = readFile("day1")
  private val funcInput = Day1Func.parser(inputString)
  private val impInput = Day1Imp.parser(inputString)
  private val fastInput = Day1Fast.parser(inputString)

  @Benchmark
  fun benchmarkFuncPart2(bh: Blackhole) {
    bh.consume(Day1Func.part2(funcInput))
  }

  @Benchmark
  fun benchmarkImpPart2(bh: Blackhole) {
    bh.consume(Day1Imp.part2(impInput))
  }

  @Benchmark
  fun benchmarkFastPart2(bh: Blackhole) {
    bh.consume(Day1Fast.part2(fastInput))
  }
}
