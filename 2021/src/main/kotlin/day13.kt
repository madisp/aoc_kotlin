import utils.IntGrid
import utils.MutableIntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day13.run()
}

object Day13 : Solution<Pair<IntGrid, List<Day13.FoldInsn>>>() {
  override val name = "day13"
  override val parser = Parser { input ->
    val (ptsLines, insnLines) = input.split("\n\n", limit = 2)

    val pts = ptsLines.split("\n").filter { it.isNotBlank() }.map {
      val (x, y) = it.split(",")
      Vec2i(x.toInt(), y.toInt())
    }

    val w = pts.maxOf { it.x } + 1
    val h = pts.maxOf { it.y } + 1

    val grid = MutableIntGrid(IntArray(w * h), w, h).apply {
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

    return@Parser (grid as IntGrid) to insns
  }

  fun foldH(grid: IntGrid, location: Int): IntGrid {
    return MutableIntGrid(IntArray(grid.width * location), grid.width, location).apply {
      coords.forEach { c ->
        val mc = Vec2i(c.x, location + (location - c.y))
        this[c] = grid[c] + if (mc in grid) grid[mc] else 0
      }
    }
  }

  fun foldV(grid: IntGrid, location: Int): IntGrid {
    return MutableIntGrid(IntArray(location * grid.height), location, grid.height).apply {
      coords.forEach { c ->
        val mc = Vec2i(location + (location - c.x), c.y)
        this[c] = grid[c] + if (mc in grid) grid[mc] else 0
      }
    }
  }

  fun fold(grid: IntGrid, insn: FoldInsn): IntGrid {
    if (insn.horizontal) {
      return foldH(grid, insn.location)
    } else {
      return foldV(grid, insn.location)
    }
  }

  data class FoldInsn(val horizontal: Boolean, val location: Int)

  override fun part1(input: Pair<IntGrid, List<FoldInsn>>): Int {
    val folded = fold(input.first, input.second.first())
    return folded.values.count { it != 0 }
  }

  override fun part2(input: Pair<IntGrid, List<FoldInsn>>): Int {
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
