import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.toVec2i

fun main() {
  Day6.run()
}

typealias Day6In = Grid<Char>

object Day6 : Solution<Day6In>() {
  override val name = "day6"
  override val parser = Parser.charGrid

  // walk the path, returns whether it loops and all the coordinates visited
  private fun getPath(block: Vec2i? = null): Pair<Boolean, Set<Vec2i>> {
    val guard = input.cells.first { (_, c) -> c in "^v><" }
    val path = mutableSetOf<Vec2i>()
    val been = mutableSetOf<Pair<Vec2i, Vec2i>>()

    var dir = guard.second.toVec2i()
    var pos = guard.first
    while (true) {
      // record
      path += pos
      // break
      if ((pos + dir) !in input) {
        return false to path
      }
      // turn
      while (input[pos + dir] == '#' || pos + dir == block) {
        if (pos to dir in been) {
          return true to path
        }
        been += pos to dir
        dir = dir.rotateCcw()
      }
      // step
      pos += dir
    }
  }

  override fun part1(input: Day6In): Int {
    return getPath().second.size
  }

  override fun part2(input: Day6In): Int {
    return getPath().second.filter { input[it] == '.' }.count { block ->
      getPath(block).first
    }
  }
}
