import utils.Parser
import utils.Solution
import utils.Point3i
import utils.Vec4i
import utils.mapItems

fun main() {
  Day18.run()
}

object Day18 : Solution<Set<Vec4i>>() {
  override val name = "day18"
  override val parser = Parser.lines.mapItems {
    val (x, y, z) = it.split(",", limit = 3).map(String::toInt)
    Point3i(x + 1, y + 1, z + 1)
  }.map(List<Vec4i>::toSet)

  override fun part1(input: Set<Vec4i>): Int {
    val space = Array(25) { Array(25) { IntArray(25) { 0 } } }

    for (cube in input) {
      space[cube.x][cube.y][cube.z] = 1
    }

    return input.flatMap { it.adjacent }.sumOf { 1 - space[it.x][it.y][it.z] }
  }

  override fun part2(input: Set<Vec4i>): Int {
    val sz = 25
    val space = Array(sz) { Array(sz) { IntArray(sz) { 0 } } }

    for (cube in input) {
      space[cube.x][cube.y][cube.z] = 1
    }

    // fill outer area with twos using 6-way fill
    val queue = ArrayDeque<Vec4i>()
    queue.add(Point3i(0, 0, 0))
    while (queue.isNotEmpty()) {
      val p = queue.removeFirst()

      if (p.x !in 0 until sz || p.y !in 0 until sz || p.z !in 0 until sz) {
        continue
      }
      if (space[p.x][p.y][p.z] != 0) {
        continue
      }

      space[p.x][p.y][p.z] = 2
      queue.addAll(p.adjacent)
    }

    return input.flatMap { it.adjacent }.sumOf { space[it.x][it.y][it.z] ushr 1 }
  }
}
