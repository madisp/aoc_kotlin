import utils.Coord
import utils.Grid
import utils.MutableGrid
import utils.Parser

fun main() {
  Day13.run()
}

object Day13 : Solution<Pair<Grid, List<Day13.FoldInsn>>> {
  override val name = "day13"
  override val parser = Parser { input ->
    val (ptsLines, insnLines) = input.split("\n\n", limit = 2)

    val pts = ptsLines.split("\n").filter { it.isNotBlank() }.map {
      val (x, y) = it.split(",")
      Coord(x.toInt(), y.toInt())
    }

    val w = pts.maxOf { it.x } + 1
    val h = pts.maxOf { it.y } + 1

    val grid = MutableGrid(IntArray(w * h), w, h).apply {
      pts.forEach { this[it] = 1 }
    }

    val insns = insnLines.split("\n").filter { it.isNotBlank() }.map {
      val (direction, location) = it.split("=")
      when (direction) {
        "fold along y" -> FoldInsn(true, location.toInt())
        "fold along x" -> FoldInsn(false, location.toInt())
        else -> throw IllegalArgumentException("Bad input")
      }
    }

    return@Parser (grid as Grid) to insns
  }

  fun foldH(grid: Grid, location: Int): Grid {
    return MutableGrid(IntArray(grid.width * location), grid.width, location).apply {
      coords.forEach { c ->
        val mc = Coord(c.x, location + (location - c.y))
        this[c] = grid[c] + if (mc in grid) grid[mc] else 0
      }
    }
  }

  fun foldV(grid: Grid, location: Int): Grid {
    return MutableGrid(IntArray(location * grid.height), location, grid.height).apply {
      coords.forEach { c ->
        val mc = Coord(location + (location - c.x), c.y)
        this[c] = grid[c] + if (mc in grid) grid[mc] else 0
      }
    }
  }

  fun fold(grid: Grid, insn: FoldInsn): Grid {
    if (insn.horizontal) {
      return foldH(grid, insn.location)
    } else {
      return foldV(grid, insn.location)
    }
  }

  data class FoldInsn(val horizontal: Boolean, val location: Int)

  override fun part1(input: Pair<Grid, List<FoldInsn>>): Int {
    val folded = fold(input.first, input.second.first())
    return folded.values.count { it != 0 }
  }

  override fun part2(input: Pair<Grid, List<FoldInsn>>): Int {
    val result = input.second.fold(input.first) { grid, insn -> fold(grid, insn) }

    for (y in 0 until result.height) {
      for (x in 0 until result.width) {
        if (result[x][y] != 0) {
          print('#')
        } else {
          print('.')
        }
      }
      println()
    }

    return 0
  }
}
