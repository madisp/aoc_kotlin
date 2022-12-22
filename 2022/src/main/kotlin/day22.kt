import Day22.Direction.DOWN
import Day22.Direction.LEFT
import Day22.Direction.RIGHT
import Day22.Direction.UP
import utils.Grid
import utils.IntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.badInput

fun main() {
  Day22.run()
}

object Day22 : Solution<Pair<Grid<Char>, List<Day22.Insn>>>() {
  override val name = "day22"
  override val parser = Parser.compound(Parser.charGrid) {
    buildList {
      var acc = 0
      for (char in it.trim()) {
        if (char.isDigit()) {
          acc = acc * 10 + char.digitToInt()
          continue
        }
        if (acc != 0) {
          add(Insn.Forward(acc))
          acc = 0
        }

        if (char == 'L') {
          add(Insn.RotateL)
        } else if (char == 'R') {
          add(Insn.RotateR)
        } else {
          badInput()
        }
      }
      if (acc != 0) {
        add(Insn.Forward(acc))
      }
    }
  }

  sealed class Insn {
    data class Forward(val amount: Int) : Insn()
    object RotateL : Insn()
    object RotateR : Insn()
  }

  enum class Direction(val v: Vec2i, val score: Int) {
    RIGHT(Vec2i(1, 0), 0),
    DOWN(Vec2i(0, 1), 1),
    LEFT(Vec2i(-1, 0), 2),
    UP(Vec2i(0, -1), 3),
  }

  data class State(
    val facing: Direction,
    val location: Vec2i,
  )

  private fun apply(grid: Grid<Char>, edgeMap: IntGrid, edgeRules: Map<Pair<Int, Direction>, Pair<Direction, (Vec2i) -> Vec2i>>, state: State, insn: Insn, part2: Boolean): State {
    return when (insn) {
      Insn.RotateR -> state.copy(facing = Direction.values().first { it.score == (state.facing.score + 1) % 4 })
      Insn.RotateL -> state.copy(facing = Direction.values().first { it.score == (state.facing.score + 3) % 4 })
      is Insn.Forward -> {
        var pos = state.location
        var direction = state.facing
        repeat(insn.amount) {
          var newPos = pos + direction.v
          var newDirection = direction

          // if ' ' or out of bounds then wrap according to direction
          if (newPos !in grid || grid[newPos] == ' ') {
            if (part2) {
              val rule = edgeRules[edgeMap[pos] to direction]!!
              newDirection = rule.first
              newPos = rule.second(pos)

//              println("Ported from ${edgeMap[pos]} $pos / $direction to ${edgeMap[newPos]} $newPos / $newDirection")
            } else {
              newPos = when (direction) {
                RIGHT -> grid.getRow(newPos.y).cells.first { it.second != ' ' }.first
                DOWN -> grid[newPos.x].cells.first { it.second != ' ' }.first
                LEFT -> grid.getRow(newPos.y).cells.last { it.second != ' ' }.first
                UP -> grid[newPos.x].cells.last { it.second != ' ' }.first
              }
            }
          }

          if (grid[newPos] == '#') {
            // if hitting a wall, don't move any more
            return@repeat
          }

          pos = newPos
          direction = newDirection
        }
        state.copy(location = pos, facing = direction)
      }
    }
  }

  override fun part1(input: Pair<Grid<Char>, List<Insn>>): Int {
    return solve(input, false)
  }

  override fun part2(input: Pair<Grid<Char>, List<Insn>>): Int {
    return solve(input, true)
  }

  private fun solve(input: Pair<Grid<Char>, List<Insn>>, part2: Boolean): Int {
    val (grid, insns) = input
    val initialPos = Vec2i(grid.getRow(0).values.indexOfFirst { it == '.' }, 0)

    var state = State(RIGHT, initialPos)

    val isTest = grid.width < 20

    val edgeMap = if (isTest) {
      IntGrid(grid.width, grid.height) { c ->
        //   1
        // 234
        //   56
        when (c / 4) {
          Vec2i(2, 0) -> 1
          Vec2i(0, 1) -> 2
          Vec2i(1, 1) -> 3
          Vec2i(2, 1) -> 4
          Vec2i(2, 2) -> 5
          Vec2i(3, 2) -> 6
          else -> 0
        }
      }
    } else {
      //  12
      //  3
      // 45
      // 6
      IntGrid(grid.width, grid.height) { c ->
        when (c / 50) {
          Vec2i(1, 0) -> 1
          Vec2i(2, 0) -> 2
          Vec2i(1, 1) -> 3
          Vec2i(0, 2) -> 4
          Vec2i(1, 2) -> 5
          Vec2i(0, 3) -> 6
          else -> 0
        }
      }
    }

    val edgeRules: Map<Pair<Int, Direction>, Pair<Direction, (Vec2i) -> Vec2i>> = if (isTest) buildMap {
      //   1
      // 234
      //   56
      put(1 to RIGHT, LEFT to { v -> Vec2i(15, 8 + (v.y - 3)) }) // to 6
      put(1 to LEFT, DOWN to { v -> Vec2i(4 + v.y, 4) }) // to 3
      put(1 to UP, DOWN to { v -> Vec2i(3 - (v.x - 8), 4) }) // to 2

      put(2 to LEFT, UP to { v -> Vec2i(12 + (7 - v.y), 11) }) // to 6
      put(2 to UP, DOWN to { v -> Vec2i(8 + (v.x - 3), 0) }) // to 1
      put(2 to DOWN, UP to { v -> Vec2i(8 + (v.x - 3), 11) }) // to 5

      put(3 to UP, RIGHT to { v -> Vec2i(8, (v.x - 4)) }) // to 1
      put(3 to DOWN, RIGHT to { v -> Vec2i(8, 4 + v.x) }) // to 5

      put(4 to RIGHT, DOWN to { v -> Vec2i(15 - (v.y - 4), 8) }) // to 6

      put(5 to LEFT, UP to { v -> Vec2i(4 + (3 - (v.y - 8)), 7) }) // to 3
      put(5 to DOWN, UP to { v -> Vec2i(3 - (v.x - 8), 7) }) // to 2

      put(6 to UP, LEFT to { v -> Vec2i(11, 4 + (3 - (v.x - 12))) }) // to 4
      put(6 to RIGHT, LEFT to { v -> Vec2i(11, (3 - (v.y - 8))) }) // to 1
      put(6 to DOWN, RIGHT to { v -> Vec2i(0, 4 + (3 - (v.x - 12))) }) // to 2
    } else buildMap {
      //  12
      //  3
      // 45
      // 6
      put(1 to LEFT, RIGHT to { v -> Vec2i(0, 100 + (49 - v.y)) }) // to 4
      put(1 to UP, RIGHT to { v -> Vec2i(0, 150 + (v.x - 50)) }) // to 6

      put(2 to UP, UP to { v -> Vec2i(v.x - 100, 199) }) // to 6
      put(2 to RIGHT, LEFT to { v -> Vec2i(99, 149 - v.y) }) // to 5
      put(2 to DOWN, LEFT to { v -> Vec2i(99, 50 + (v.x - 100)) }) // to 3

      put(3 to LEFT, DOWN to { v -> Vec2i(v.y - 50, 100) }) // to 4
      put(3 to RIGHT, UP to { v -> Vec2i(v.y - 50 + 100, 49) }) // to 2

      put(4 to LEFT, RIGHT to { v -> Vec2i(50, 49 - (v.y - 100)) }) // to 1
      put(4 to UP, RIGHT to { v -> Vec2i(50, 50 + v.x) }) // to 3

      put(5 to RIGHT, LEFT to { v -> Vec2i(149, 49 - (v.y - 100)) }) // to 2
      put(5 to DOWN, LEFT to { v -> Vec2i(49, 150 + (v.x - 50)) }) // to 6

      put(6 to RIGHT, UP to { v -> Vec2i(50 + (v.y - 150), 149) }) // to 5
      put(6 to DOWN, DOWN to { v -> Vec2i(100 + v.x, 0) }) // to 2
      put(6 to LEFT, DOWN to { v -> Vec2i(50 + (v.y - 150), 0) }) // to 1
    }

    insns.forEach {
      state = apply(grid, edgeMap, edgeRules, state, it, part2)
    }

    return state.facing.score + (state.location.y + 1) * 1000 + (state.location.x + 1) * 4
  }
}
