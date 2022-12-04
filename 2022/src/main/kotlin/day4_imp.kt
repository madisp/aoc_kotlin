import utils.Parser
import utils.Solution
import utils.cut
import utils.map
import utils.mapItems

fun main() {
  Day4Imp.run()
}

object Day4Imp : Solution<Day4Input>() {
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
    var count = 0
    for (pair in input) {
      if (pair.first in pair.second || pair.second in pair.first) {
        count++
      }
    }
    return count
  }

  override fun part2(input: Day4Input): Int {
    var count = 0
    for ((a, b) in input) {
      if (minOf(a.last, b.last) >= maxOf(a.first, b.first)) {
        count++
      }
    }
    return count
  }
}
