import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.mapItems

fun main() {
  Day1.run()
}

object Day1 : Solution<List<String>>() {
  override val name = "day1"
  override val parser = Parser { it.split(", ") }.mapItems { it.trim() }

  private fun locations(): Sequence<Vec2i> {
    var direction = Vec2i(0, 1)
    var location = Vec2i(0, 0)
    return sequence {
      input.forEach {
        when(it[0]) {
          'L' -> { direction = direction.rotateCw() }
          'R' -> { direction = direction.rotateCcw() }
        }
        val dist = it.substring(1).toInt()
        repeat(dist) {
          location += direction
          yield(location)
        }
      }
    }
  }

  override fun part1(): Int {
    return locations().last().manhattanDistanceTo(Vec2i(0, 0))
  }

  override fun part2(): Any? {
    val visited = mutableSetOf(Vec2i(0, 0))

    locations().forEach {
      if (it in visited) {
        return it.manhattanDistanceTo(Vec2i(0, 0))
      }
      visited += it
    }

    return null
  }
}
