import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.withDefault

fun main() {
  Day4.run()
}

typealias Day4In = Grid<Char>

object Day4 : Solution<Day4In>() {
  override val name = "day4"
  override val parser: Parser<Day4In> = Parser.charGrid.map { it.withDefault('.') }

  override fun part1(input: Day4In): Int {
    return input.coords.sumOf { p ->
      (Vec2i.CARDINALS + Vec2i.DIAGONALS).count { dir ->
        (0 .. 3).map { input[p + (dir * it)] } == listOf('X', 'M', 'A', 'S')
      }
    }
  }

  override fun part2(input: Day4In): Int {
    return input.coords.sumOf { p ->
      (Vec2i.DIAGONALS).count { diag ->
        listOf(diag, diag.rotateCw()).all { dir ->
          (-1 .. 1).map { input[p + (dir * it)] } == listOf('M', 'A', 'S')
        }
      }
    }
  }
}
