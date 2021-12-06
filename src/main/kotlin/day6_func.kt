fun main() {
  Day6Func.run()
}

object Day6Func : Solution<Map<Int, Long>> {
  override val name = "day6"
  override val parser = Parser { input ->
    input.split(",").map { it.toInt() }
      .groupBy { it }
      .mapValues { it.value.size.toLong() }
  }

  override fun part1(input: Map<Int, Long>): Long {
    return simulate(input, forDays = 80).values.sum()
  }

  override fun part2(input: Map<Int, Long>): Long {
    return simulate(input, forDays = 256).values.sum()
  }

  private fun simulate(input: Map<Int, Long>, forDays: Int): Map<Int, Long> {
    return (1 .. forDays).fold(input) { acc, _ -> simulate(acc) }
  }

  private fun simulate(input: Map<Int, Long>): Map<Int, Long> {
    return input.entries
      // split the entries into two - the ones with internal timer of 0 and the normal ones
      .flatMap { (day, count) ->
        if (day == 0) {
          listOf(6 to count, 8 to count)
        } else {
          listOf(day - 1 to count)
        }
      }
      // we've got duplicate keyed pairs in these lists now, group by the internal timer value and sum the two
      .groupBy { it.first }
      .mapValues { (_, v) -> v.sumOf { it.second } }
      .toMap()
  }
}