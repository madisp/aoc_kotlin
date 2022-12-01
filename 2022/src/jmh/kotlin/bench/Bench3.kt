package bench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole
import utils.Solution
import utils.readFile

abstract class Bench3<Func, Imp, Fast>(
  day: String,
  private val func: Solution<Func>,
  private val imp: Solution<Imp>,
  private val fast: Solution<Fast>
) {
  private val input = readFile(day)
  private val funcInput = func.parser(input)
  private val impInput = imp.parser(input)
  private val fastInput = fast.parser(input)

  @Benchmark
  fun benchmarkFuncPart2(bh: Blackhole) {
    bh.consume(func.part2(funcInput))
  }

  @Benchmark
  fun benchmarkImpPart2(bh: Blackhole) {
    bh.consume(imp.part2(impInput))
  }

  @Benchmark
  fun benchmarkFastPart2(bh: Blackhole) {
    bh.consume(fast.part2(fastInput))
  }
}
