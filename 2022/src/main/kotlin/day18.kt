import utils.MutableIntSpace
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

  private fun makeSpace(input: Set<Vec4i>) = MutableIntSpace(
    input.maxOf { it.x } + 2,
    input.maxOf { it.y } + 2,
    input.maxOf { it.z } + 2) {
      if (it in input) 1 else 0 // init known blocks to 1
    }

  override fun part1(input: Set<Vec4i>): Int {
    val space = makeSpace(input)
    return input.flatMap { it.adjacent }.sumOf { 1 - space[it] }
  }

  override fun part2(input: Set<Vec4i>): Int {
    val space = makeSpace(input)

    // fill outer area with tows using 6-way fill
    val queue = ArrayDeque<Vec4i>()
    queue.add(Point3i(0, 0, 0))
    while (queue.isNotEmpty()) {
      val p = queue.removeFirst()
      if (space[p] != 0) continue
      space[p] = 2
      queue.addAll(p.adjacent.filter { it in space })
    }

    return input.flatMap { it.adjacent }.sumOf { space[it] ushr 1 }
  }
}
