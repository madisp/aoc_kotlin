import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.withDefault

fun main() {
  Day10.run()
}

typealias Day10In = Grid<Char>

object Day10 : Solution<Day10In>() {
  override val name = "day10"
  override val parser: Parser<Day10In> = Parser.charGrid.map { it.withDefault('.') }

  private fun countReachableSummits(from: Vec2i): Set<Vec2i> {
    val height = input[from]
    if (height == '9') {
      return setOf(from)
    }
    return from.adjacent.filter { input[it] == height + 1 }.fold(emptySet()) { acc, p ->
      acc + countReachableSummits(p)
    }
  }

  override fun part1(input: Day10In): Int {
    val trailheads = input.coords.filter { input[it] == '0' }
    return trailheads.sumOf { countReachableSummits(it).size }
  }

  private fun countPaths(from: Vec2i): Int {
    val height = input[from]
    if (height == '9') {
      return 1
    }
    return from.adjacent.filter { input[it] == height + 1 }.sumOf {
      countPaths(it)
    }
  }

  override fun part2(input: Day10In): Int {
    val trailheads = input.coords.filter { input[it] == '0' }
    return trailheads.sumOf { countPaths(it) }
  }
}
