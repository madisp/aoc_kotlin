import utils.*

fun main() {
  Day18.run()
}

typealias Day18In = List<Vec2i>

object Day18 : Solution<Day18In>() {
  override val name = "day18"
  override val parser: Parser<Day18In> = Parser.lines.mapItems { Vec2i.parse(it) }

  private fun shortestPathLen(numBytes: Int): Int? {
    val dimen = if (input.size <= 25) { Vec2i(7,7) } else { Vec2i(71, 71) }
    val grid = createMutableGrid(dimen.x, dimen.y, Grid.OobBehaviour.Default('#')) { '.' }

    input.take(numBytes).forEach { p ->
      grid[p] = '#'
    }

    val g = Graph<Vec2i, Unit>(
      edgeFn = { p ->
        p.adjacent.filter { grid[it] != '#' }.map { Unit to it }
      }
    )

    return runCatching {
      g.shortestPath(Vec2i(0, 0), Vec2i(grid.width - 1, grid.height - 1)).first
    }.getOrNull()
  }

  override fun part1(input: Day18In): Any? {
    val numBytes = if (input.size <= 25) 12 else 1024
    return shortestPathLen(numBytes)
  }

  override fun part2(input: Day18In): Any? {
    var (start, end) = (if (input.size <= 25) 12 else 1024) to input.size

    while (end - start > 1) {
      val middle = start + (end - start) / 2
      if (shortestPathLen(middle) == null) {
        end = middle
      } else {
        start = middle
      }
    }

    return "${input[start].x},${input[start].y}"
  }
}
