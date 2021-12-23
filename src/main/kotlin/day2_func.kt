import utils.Parser
import utils.mapItems

fun main() {
  Day2Func.run()
}

object Day2Func : Solution<List<Pair<Day2Func.Direction, Int>>> {
  override val name = "day2"
  override val parser = Parser.lines.mapItems { line ->
    val (direction, amountStr) = line.split(" ")
    Direction.valueOf(direction) to amountStr.toInt()
  }

  enum class Direction { forward, down, up }

  data class State(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0)

  override fun part1(input: List<Pair<Direction, Int>>): Number {
    val final = input.fold(State()) { state, cmd ->
      when (cmd.first) {
        Direction.forward -> state.copy(horizontal = state.horizontal + cmd.second)
        Direction.down -> state.copy(depth = state.depth + cmd.second)
        Direction.up -> state.copy(depth = state.depth - cmd.second)
      }
    }
    return final.depth * final.horizontal
  }

  override fun part2(input: List<Pair<Direction, Int>>): Number? {
    val final = input.fold(State()) { state, cmd ->
      when (cmd.first) {
        Direction.forward -> state.copy(
          horizontal = state.horizontal + cmd.second,
          depth = state.depth + state.aim * cmd.second
        )
        Direction.down -> state.copy(aim = state.aim + cmd.second)
        Direction.up -> state.copy(aim = state.aim - cmd.second)
      }
    }
    return final.depth * final.horizontal
  }
}
