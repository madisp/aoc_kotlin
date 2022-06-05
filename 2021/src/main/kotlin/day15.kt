import utils.Grid
import utils.Solution
import utils.Vec2i
import java.util.PriorityQueue

fun main() {
  Day15.run()
}

object Day15 : Solution<Grid>() {
  override val name = "day15"
  override val parser = Grid.singleDigits

  data class Item(val coord: Vec2i, val risk: Int)

  override fun part1(input: Grid): Int {
    return solve(input)
  }

  override fun part2(input: Grid): Number? {
    val wide = Grid(input.width * 5, input.height * 5) { (x, y) ->
      val dist = (x / input.width) + (y / input.height)
      val orig = input[x % input.width][y % input.height]
      val new = orig + dist

      if (new > 9) new - 9 else new
    }

    return solve(wide)
  }

  private fun solve(input: Grid): Int {
    val end = Vec2i(input.width - 1, input.height - 1)

    val visited = mutableSetOf<Vec2i>()
    val queue = PriorityQueue<Item>(compareBy { it.risk })
    queue.add(Item(Vec2i(0, 0), 0))

    while (queue.isNotEmpty()) {
      val (coord, currentRisk) = queue.remove()
      if (coord in visited) continue

      if (coord == end) {
        return currentRisk
      }

      visited.add(coord)

      coord.adjacent.filter { it in input }.forEach {
        queue.add(Item(it, currentRisk + input[it]))
      }
    }

    throw IllegalStateException("Should not happen, is there a path from start -> end")
  }
}
