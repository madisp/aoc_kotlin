import utils.Grid
import utils.Parser
import utils.Solution
import utils.withCounts
import java.security.MessageDigest

fun main() {
  Day6.run()
}

object Day6 : Solution<Grid<Char>>() {
  override val name = "day6"
  override val parser = Parser.charGrid

  override fun part1(): String {
    return input.columns.map {
      it.values.withCounts().entries.maxByOrNull { it.value }!!.key
    }.joinToString("")
  }

  override fun part2(): String {
    return input.columns.map {
      it.values.withCounts().entries.minByOrNull { it.value }!!.key
    }.joinToString("")
  }
}
