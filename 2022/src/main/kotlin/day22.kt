import Day22.Direction.DOWN
import Day22.Direction.LEFT
import Day22.Direction.RIGHT
import Day22.Direction.UP
import utils.Grid
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

  private fun apply(grid: Grid<Char>, state: State, insn: Insn): State {
    return when (insn) {
      Insn.RotateR -> state.copy(facing = Direction.values().first { it.score == (state.facing.score + 1) % 4 })
      Insn.RotateL -> state.copy(facing = Direction.values().first { it.score == (state.facing.score + 3) % 4 })
      is Insn.Forward -> {
        var pos = state.location
        var direction = state.facing
        repeat(insn.amount) {
          var newPos = pos + state.facing.v

          // if ' ' or out of bounds then wrap according to direction
          if (newPos !in grid || grid[newPos] == ' ') {
            newPos = when (state.facing) {
              RIGHT -> grid.getRow(newPos.y).cells.first { it.second != ' ' }.first
              DOWN -> grid[newPos.x].cells.first { it.second != ' ' }.first
              LEFT -> grid.getRow(newPos.y).cells.last { it.second != ' ' }.first
              UP -> grid[newPos.x].cells.last { it.second != ' ' }.first
            }
          }

          if (grid[newPos] == '#') {
            // if hitting a wall, break
            return@repeat
          }

          pos = newPos
        }
        state.copy(location = pos)
      }
    }
  }

  override fun part1(input: Pair<Grid<Char>, List<Insn>>): Int {
    val (grid, insns) = input
    val initialPos = Vec2i(grid.getRow(0).values.indexOfFirst { it == '.' }, 0)

    var state = State(RIGHT, initialPos)
    println(state)

    insns.forEach {
      state = apply(grid, state, it)
//      println("$it")
//      println("$state")
    }

    return state.facing.score + (state.location.y + 1) * 1000 + (state.location.x + 1) * 4
  }
}
