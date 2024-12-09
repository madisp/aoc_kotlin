import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.withDefault

fun main() {
  Day9.run()
}

typealias Day9In = Grid<Char>

object Day9 : Solution<Day9In>() {
  override val name = "day9"
  override val parser: Parser<Day9In> = Parser.charGrid.map { it.withDefault('0') }

  override fun part1(input: Day9In): Int {
    val sqr = listOf(Vec2i(0, 0), Vec2i.RIGHT, Vec2i.DOWN, Vec2i.DOWN + Vec2i.RIGHT)
    return input.coords.count { p -> sqr.all { input[p + it] == '1' } }
  }
}
