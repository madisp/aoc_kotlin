import utils.Parser
import utils.Solution
import utils.cut
import utils.map
import utils.mapItems

fun main() {
  Day4Func.run()
}

typealias Day4Input = List<Pair<IntRange, IntRange>>

operator fun IntRange.contains(other: IntRange): Boolean {
  return (other.first in this && other.last in this)
}

object Day4Func : Solution<Day4Input>() {
  override val name = "day4"
  override val parser = Parser.lines.mapItems { line ->
    line.cut(",")
  }.mapItems {
    it.map { segment ->
      val (start,end) = segment.cut("-").map { it.toInt() }
      start .. end
    }
  }

  override fun part1(input: Day4Input): Int {
    return input.count { it.first in it.second || it.second in it.first }
  }

  override fun part2(input: Day4Input): Int {
    return input.count { (it.first intersect  it.second).isNotEmpty() }
  }
}
