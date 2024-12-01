import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems
import utils.toInts
import utils.withCounts
import kotlin.math.absoluteValue

fun main() {
  Day1.run()
}

typealias Day1In = Pair<List<Int>, List<Int>>

object Day1 : Solution<Day1In>() {
  override val name = "day1"
  override val parser: Parser<Day1In> = Parser.lines.mapItems {
    it.cut(" ").toInts()
  }.map {
    it.unzip()
  }

  override fun part1(input: Day1In): Int {
    val (first, second) = input
    return first.sorted().zip(second.sorted()).sumOf { (a, b) -> (a - b).absoluteValue }
  }

  override fun part2(input: Day1In): Int {
    val counts = input.second.withCounts()
    return input.first.sumOf { (counts[it]) * it }
  }
}
