import utils.Grid
import utils.Parser
import utils.Solution

fun main() {
  Day15.run()
}

typealias Day15In = Pair<String, Grid<Char>>

object Day15 : Solution<Day15In>() {
  override val name = "day15"
  override val parser: Parser<Day15In> = Parser.compound(
    delimiter = "\n",
    first = { it.trim() + "+" },
    second = Parser.charGrid)

  override fun part1(input: Day15In): Int {
    val (str, grid) = input
    val start = grid.coords.first { grid[it] == '-' }
    return str.fold(start to 0) { (pos, dist), c ->
      val newPos = grid.coords.first { grid[it] == c }
      newPos to (dist + pos.manhattanDistanceTo(newPos))
    }.second
  }
}
