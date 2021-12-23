import utils.Grid

fun main() {
  Day3.run()
}

object Day3 : Solution<Grid> {
  override val name = "day3"
  override val parser = Grid.singleDigits

  override fun part1(input: Grid): Int {
    val gamma = (0 until input.width).map { index ->
      getPop(input[index].values).first
    }.joinToString(separator = "").toInt(2)

    val epsilon = (0 until input.width).map { index ->
      getPop(input[index].values).second
    }.joinToString(separator = "").toInt(2)

    return gamma * epsilon
  }

  override fun part2(input: Grid): Int {
    val oxygen = generateSequence(emptyList<Int>() to input.rows) { (mask, rows) ->
      if (mask.size == input.width) return@generateSequence null
      val newMask = mask + getPop(rows.map { it[mask.size] }).first
      (newMask to rows.filter { it.values.startsWith(newMask) }).takeIf { it.second.isNotEmpty() }
    }.last().second.first().values.joinToString("").toInt(2)

    val co2 = generateSequence(emptyList<Int>() to input.rows) { (mask, rows) ->
      if (mask.size == input.width) return@generateSequence null
      val newMask = mask + getPop(rows.map { it[mask.size] }).second
      (newMask to rows.filter { it.values.startsWith(newMask) }).takeIf { it.second.isNotEmpty() }
    }.last().second.first().values.joinToString("").toInt(2)

    return oxygen * co2 ///co2
  }


  /**
   * Returns 1, 0 if there's at least as many ones as zeroes in strings at pos
   * else returns 0, 1
   */
  private fun getPop(values: Collection<Int>): Pair<Int, Int> {
    val ones = values.count { it == 1 }
    val zeroes = values.count { it == 0 }
    return if (ones >= zeroes) 1 to 0 else 0 to 1
  }
}
