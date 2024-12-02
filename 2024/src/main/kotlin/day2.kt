import utils.Parser
import utils.Solution
import utils.parseItems
import utils.selections
import kotlin.math.absoluteValue

fun main() {
  Day2.run()
}

typealias Day2In = List<List<Int>>

object Day2 : Solution<Day2In>() {
  override val name = "day2"
  override val parser: Parser<Day2In> = Parser.lines.parseItems(Parser.spacedInts)

  private fun isSafe(report: List<Int>): Boolean {
    if (report.sorted() != report && report.sortedDescending() != report) {
      return false
    }
    return report.windowed(2).all { (a, b) -> (a - b).absoluteValue in 1..3 }
  }

  override fun part1(input: Day2In): Int {
    return input.count { isSafe(it) }
  }

  override fun part2(input: Day2In): Int {
    return input.count { line ->
      line.selections(line.size - 1).any { isSafe(it) }
    }
  }
}
