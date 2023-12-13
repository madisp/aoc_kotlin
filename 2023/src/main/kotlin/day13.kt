import utils.Grid
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.flipAxis

fun main() {
  Day13.run()
}

object Day13 : Solution<List<Grid<Char>>>() {
  override val name = "day13"
  override val parser = Parser.blocks.mapItems { Parser.charGrid(it) }

  private fun reflects(g: Grid<Char>, x: Int, errors: Int): Boolean {
    val len = minOf(x, g.width - x)

    return (0 until len).sumOf {
      g[x - it - 1].values.zip(g[x + it].values).count { (a, b) -> a != b }
    } == errors
  }

  private fun solve(errors: Int = 0): Int {
    return input.sumOf { line ->
      listOf(line to 1, line.flipAxis() to 100).sumOf { (g, mul) ->
        (1 until g.width).sumOf { if (reflects(g, it, errors)) it else 0 } * mul
      }
    }
  }

  override fun part1() = solve()
  override fun part2() = solve(errors = 1)
}
