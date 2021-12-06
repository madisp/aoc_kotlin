fun main() {
  Day3.run()
}

object Day3 : Solution<List<String>> {
  override val name = "day3"
  override val parser = Parser.lines

  override fun part1(input: List<String>): Int {
    val len = input.first().trim().length

    val gamma = (0 until len).map { index ->
      getPop(input, index).first
    }.joinToString(separator = "").toInt(2)

    val epsilon = (0 until len).map { index ->
      getPop(input, index).second
    }.joinToString(separator = "").toInt(2)

    return gamma * epsilon
  }

  override fun part2(input: List<String>): Int {
    val len = input.first().trim().length

    val oxygen = (0 until len).fold(input.toSet()) { acc, index ->
      val pop = getPop(acc, index).first
      if (acc.size == 1) { acc } else {
        acc.intersect(input.filter { it[index] == pop }.toSet())
      }
    }.first().toInt(2)

    val co2 = (0 until len).fold(input.toSet()) { acc, index ->
      val pop = getPop(acc, index).second
      if (acc.size == 1) { acc } else {
        acc.intersect(input.filter { it[index] == pop }.toSet())
      }
    }.first().toInt(2)

    return oxygen * co2
  }

  /**
   * Returns 1, 0 if there's at least as many ones as zeroes in strings at pos
   * else returns 0, 1
   */
  private fun getPop(strings: Collection<String>, pos: Int): Pair<Char, Char> {
    val ones = strings.count { it[pos] == '1' }
    val zeroes = strings.count { it[pos] == '0' }
    return if (ones >= zeroes) '1' to '0' else '0' to '1'
  }
}
