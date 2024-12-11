import utils.Parser
import utils.Solution

fun main() {
  Day11.run()
}

typealias Day11In = List<Long>

object Day11 : Solution<Day11In>() {
  override val name = "day11"
  override val parser: Parser<Day11In> = Parser.spacedLongs

  private fun countStones(blinkTimes: Int, stone: Long, memo: MutableMap<Pair<Int, Long>, Long> = mutableMapOf()): Long {
    if (blinkTimes == 0) { return 1L }
    memo[blinkTimes to stone]?.let { return it }
    val str = stone.toString()
    return when {
      stone == 0L -> countStones(blinkTimes - 1, 1L, memo)
      str.length % 2 == 0 -> {
        countStones(blinkTimes - 1, str.substring(0, str.length / 2).toLong(), memo) +
          countStones(blinkTimes - 1, str.substring(str.length / 2, str.length).toLong(), memo)
      }
      else -> countStones(blinkTimes - 1, 2024L * stone, memo)
    }.also { memo[blinkTimes to stone] = it }
  }

  override fun part1(input: Day11In): Long {
    return input.sumOf { countStones(25, it) }
  }

  override fun part2(input: Day11In): Long {
  return input.sumOf { countStones(75, it) }
  }
}
