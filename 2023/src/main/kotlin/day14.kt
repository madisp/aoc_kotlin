import utils.Grid
import utils.MutableGrid
import utils.Parser
import utils.Solution
import utils.debugString
import utils.rotateCw
import utils.toMutable

fun main() {
  Day14.run()
}

object Day14 : Solution<Grid<Char>>() {
  override val name = "day14"
  override val parser = Parser.charGrid

  override fun part1(): Int {
    val g = input.toMutable()
    tiltNorth(g)
    return score(g)
  }

  override fun part2(): Int {
    val limit = 1_000_000_000

    val seenAt = mutableMapOf<String, Int>()
    val scores = mutableListOf<Int>()

    var g = input.toMutable()
    var loop = 0 to 0

    for (i in 0 until limit) {
      g = cycle(g)
      scores.add(score(g))
      val loopStart = seenAt[g.debugString]
      if (loopStart != null) {
        loop = loopStart to i
        break
      }
      seenAt[g.debugString] = i
    }

    return scores[loop.first - 1 + (limit - loop.first) % (loop.second - loop.first)]
  }

  private fun cycle(grid: MutableGrid<Char>): MutableGrid<Char> {
    var g = grid
    repeat(4) {
      tiltNorth(g)
      g = g.rotateCw().toMutable()
    }
    return g
  }

  private fun tiltNorth(g: MutableGrid<Char>) {
    for (x in 0 until g.width) {
      var lastEdge = 0

      for (y in 0 until g.height) {
        when (g[x][y]) {
          '#' -> lastEdge = y + 1
          'O' -> {
            if (lastEdge != y) {
              g[x][lastEdge++] = 'O'
              g[x][y] = '.'
            } else {
              lastEdge++
            }
          }
          else -> { /* ignore */ }
        }
      }
    }
  }

  private fun score(g: Grid<Char>) = g.cells.sumOf { (p, c) -> if (c != 'O') 0 else (g.height - p.y) }
}
