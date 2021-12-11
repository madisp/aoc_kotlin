import utils.Parser

fun main() {
  Day6Imp.run()
}

object Day6Imp : Solution<Map<Int, Long>> {
  override val name = "day6"
  override val parser = Parser { input ->
    input.split(",").map { it.toInt() }
      .groupBy { it }
      .mapValues { it.value.size.toLong() }
  }

  override fun part1(input: Map<Int, Long>): Long {
    return simulate(input, forDays = 80).sum()
  }

  override fun part2(input: Map<Int, Long>): Long {
    return simulate(input, forDays = 256).sum()
  }

  private fun simulate(input: Map<Int, Long>, forDays: Int): LongArray {
    val state = LongArray(10) { index -> input[index] ?: 0L }

    for (i in 0 until forDays) {
      val count = state[0]
      state[9] = (state[9]) + count
      state[7] = (state[7]) + count
      state[0] = 0
      for (j in 1 .. 9) {
        state[j - 1] = state[j]
      }
      state[9] = 0
    }

    return state
  }
}