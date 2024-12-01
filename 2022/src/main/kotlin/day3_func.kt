import utils.Parser
import utils.Solution
import utils.map
import utils.mapItems
import utils.split

fun main() {
  Day3Func.run()
}

typealias Day3Input = List<Pair<Set<Char>, Set<Char>>>

val Char.priority: Int get() = when (this) {
  in ('A' .. 'Z') -> this - 'A' + 26
  else -> this - 'a'
}

object Day3Func : Solution<Day3Input>() {
  override val name = "day3"
  override val parser = Parser.lines.mapItems { line -> line.split().map { it.toCharArray().toSet() } }

  override fun part1(input: Day3Input): Int {
    return input
      .flatMap { sets -> sets.first intersect sets.second }
      .sumOf { it.priority + 1 }
  }

  override fun part2(input: Day3Input): Int {
    return input
      .chunked(3)
      .flatMap { group ->
        group.map { (x, y) -> x + y }
          .reduce { acc, set -> acc intersect set }
      }
      .sumOf { it.priority + 1 }
  }
}
