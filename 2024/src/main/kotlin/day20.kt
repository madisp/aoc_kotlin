import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.createMutableGrid
import java.util.PriorityQueue

fun main() {
  Day20.run()
}

typealias Day20In = Grid<Char>

object Day20 : Solution<Day20In>() {
  override val name = "day20"
  override val parser: Parser<Day20In> = Parser.charGrid

  private fun distGrid(start: Vec2i): Grid<Int> {
    val g = createMutableGrid(input.width, input.height) { Int.MAX_VALUE }
    val queue = PriorityQueue<Pair<Int, Vec2i>>(compareBy { it.first })
    queue.add(0 to start)
    while (queue.isNotEmpty()) {
      val (dist, p) = queue.poll()
      if (dist < g[p]) {
        g[p] = dist
        queue.addAll(p.adjacent.filter { input[it] in "SE." && dist + 1 < g[it] }.map { (dist + 1) to it })
      }
    }
    return g
  }

  private fun solve(cheatPico: Int, improveTarget: Int): Int {
    val start = input.coords.first { input[it] == 'S' }
    val end = input.coords.first { input[it] == 'E' }
    val cheatStarts = distGrid(start)
    val cheatEnds = distGrid(end)
    val nocheat = cheatEnds[start]

    return cheatStarts.coords.sumOf { cheatStart ->
      cheatEnds.coords.count { cheatEnd ->
        val startLen = cheatStarts[cheatStart].takeIf { it != Int.MAX_VALUE } ?: return@count false
        val endLen = cheatEnds[cheatEnd].takeIf { it != Int.MAX_VALUE } ?: return@count false
        val p = cheatStart.manhattanDistanceTo(cheatEnd)
        p <= cheatPico && (startLen + p + endLen) <= nocheat - improveTarget
      }
    }
  }

  override fun part1(input: Day20In): Int {
    return solve(2, if (input.height < 100) 50 else 100)
  }

  override fun part2(input: Day20In): Int {
    return solve(20, if (input.height < 100) 50 else 100)
  }
}
