/**
 * Returns 1, 0 if there's at least as many ones as zeroes in strings at pos
 * else returns 0, 1
 */
fun getPop(strings: Collection<String>, pos: Int): Pair<Char, Char> {
  val ones = strings.count { it[pos] == '1' }
  val zeroes = strings.count { it[pos] == '0' }
  return if (ones > zeroes) '1' to '0' else '0' to '1'
}

fun main() {
  val lines = readFile("day3_test").lines().filter { it.isNotBlank() }

  val len = lines.first().trim().length

  val gamma = (0 until len).map { index ->
    getPop(lines, index).first
  }.joinToString(separator = "").toInt(2)

  val epsilon = (0 until len).map { index ->
    getPop(lines, index).second
  }.joinToString(separator = "").toInt(2)

  println("part1 (functional):")
  println(gamma * epsilon)

  val oxygen = (0 until len).fold(lines.toSet()) { acc, index ->
    val pop = getPop(lines, index).first
    if (acc.size == 1) { acc } else {
      acc.intersect(lines.filter { it[index] == pop }.toSet())
    }
  }.first().toInt(2)

  val co2 = (0 until len).fold(lines.toSet()) { acc, index ->
    val pop = getPop(lines, index).second
    if (acc.size == 1) { acc } else {
      acc.intersect(lines.filter { it[index] == pop }.toSet())
    }
  }.first().toInt(2)

  println("part2:")
  println(oxygen * co2)

  // ---

  val counts = Array(size = len) { 0 to 0 }
  val ints = lines.map { it.toInt(2) }
  ints.forEach { line ->
    (0 until len).forEach { index ->
      val mask = 1 shl (len - index - 1)
      if (line and mask != 0) {
        counts[index] = counts[index].copy(first = counts[index].first + 1)
      } else {
        counts[index] = counts[index].copy(second = counts[index].second + 1)
      }
    }
  }

  val igamma = counts.mapIndexed { index, it ->
    if (it.first > it.second) 1 shl (len-index-1) else 0
  }.sum()
  val iepsilon = counts.mapIndexed { index, it ->
    if (it.first < it.second) 1 shl (len-index-1) else 0
  }.sum()

  println("part1 (imperative):")
  println(igamma * iepsilon)

  println("part2 (imperative):")
  val ioxygen = ints.toMutableSet()
  val ico2 = ints.toMutableSet()

  counts.forEachIndexed { index, (ones, zeroes) ->
    val mask = 1 shl (len - index - 1)

    if (ioxygen.size > 1) {
      if (ones >= zeroes) {
        ioxygen.removeIf { it and mask == 0 }
      } else {
        ioxygen.removeIf { it and mask != 0 }
      }
    }

    if (ico2.size > 1) {
      if (ones >= zeroes) {
        ico2.removeIf { it and mask != 0 }
      } else {
        ico2.removeIf { it and mask == 0 }
      }
    }
  }

  println(ioxygen.first() * ico2.first())

  // debug:
//  val ints = lines.map { it.toInt(2) }
//
//  val gamma = (0 until len).map { index ->
//    ints.map { it and (1 shl index) }
//      .groupBy { it }
//      .map { (key, values) -> key to values.count() }
//      .maxByOrNull { (_, count) -> count }!!
//      .first
//  }.reduce { a, b -> a or b }
//
//  val epsilon = (0 until 5).map { index ->
//    ints.map { it and (1 shl index) }
//      .groupBy { it }
//      .map { (key, values) -> key to values.count() }
//      .minByOrNull { (_, count) -> count }!!
//      .first
//  }.reduce { a, b -> a or b }
}
