import utils.Parser
import utils.Solution
import utils.mapItems
import kotlin.math.absoluteValue

fun main() {
  Day1.run()
}

typealias Day1In = List<Int>

object Day1 : Solution<Day1In>() {
  override val name = "day1"
  override val parser: Parser<Day1In> = Parser.lines.mapItems {
    val neg = it.startsWith("L")
    val num = it.drop(1).toInt()
    if (neg) -num else num
  }

  override fun part1(input: Day1In): Int {
    var state = 50
    return input.sumOf { num ->
      state = (100 + state + num) % 100
      if (state == 0) 1.toInt() else 0
    }
  }

  override fun part2(input: Day1In): Int {
    var state = 50
    return input.sumOf { num ->
      crosses(state, num).also {
        state = (100 + state + norm(num).first) % 100
      }
    }
  }

  private fun norm(value: Int): Pair<Int, Int> {
    val crosses = value.absoluteValue / 100
    return value % 100 to crosses
  }

  private fun crosses(state: Int, delta: Int): Int {
    val (normDelta, normCrosses) = norm(delta)
    val crosses = if (state != 0 && (state + normDelta !in 1..<100)) 1 else 0
    return normCrosses + crosses
  }
}
