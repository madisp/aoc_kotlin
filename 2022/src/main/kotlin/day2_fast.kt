import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day2Fast.run()
}

object Day2Fast : Solution<IntArray>() {
  override val name = "day2"

  // bit layout:
  // 0b0000 XXYY
  override val parser = Parser.lines.mapItems { line ->
    val lookUp = listOf("A", "B", "C", "X", "Y", "Z")
    line.cut(" ") { l -> lookUp.indexOf(l) }
  }.map { it.map { p -> ((p.first) + ((p.second - 3) shl 2)) }.toIntArray() }

  override fun part1(input: IntArray): Long {
    // packed nibbles of scores
    val lookup = 0x69302580714
    var score = 0L
    input.forEach {
      score += (lookup ushr (it shl 2)) and 15
    }
    return score
  }

  override fun part2(input: IntArray): Long {
    // packed nibbles of scores
    val lookup = 0x79806540213
    var score = 0L
    input.forEach {
      score += (lookup ushr (it shl 2)) and 15
    }
    return score
  }
}
