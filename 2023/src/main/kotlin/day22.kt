import utils.Cuboid
import utils.MutableIntSpace
import utils.Parser
import utils.Point3i
import utils.Solution
import utils.Vec4i
import utils.bounds
import utils.cut
import utils.map
import utils.mapItems

fun main() {
  Day22.run()
}

object Day22 : Solution<List<Cuboid>>() {
  override val name = "day22"
  override val parser = Parser.lines.mapItems {
    val (start, end) = it.cut("~").map(::parseVec4i)
    Cuboid(start, end)
  }

  fun parseVec4i(input: String): Vec4i {
    val (x, y, z) = input.split(",").map { it.toInt() }
    return Point3i(x, y, z)
  }

  override fun part1(): Int {
    val restsOn = getRestsGraph()

    val safeToDisintegrate = mutableSetOf<Int>()
    safeToDisintegrate.addAll(input.indices)

    restsOn.forEach { ints ->
      if (ints.size == 1) {
        // sole support for some brick
        safeToDisintegrate.remove(ints.first())
      }
    }

    return safeToDisintegrate.size
  }

  override fun part2(): Int {
    val restsOn = getRestsGraph()

    // flip the restsOn graph
    val supports = Array(input.size) { mutableSetOf<Int>() }
    restsOn.forEachIndexed { index, supportingBricks ->
      supportingBricks.forEach { support ->
        supports[support] += index
      }
    }

    val supportingBricks = restsOn.filter { it.size == 1 }.map { it.first() }.toSet()
    val painted = Array(input.size) { mutableSetOf<Int>() }

    supportingBricks.forEach { supportBrick ->
      val queue = ArrayDeque(supports[supportBrick].filter { restsOn[it].size == 1 })
      while (queue.isNotEmpty()) {
        val brick = queue.removeFirst()
        painted[supportBrick] += brick
        queue.addAll(supports[brick].filter { restsOn[it].all { b -> b in painted[supportBrick] } })
      }
    }

    val sum = painted.sumOf { it.size }
    return sum
  }

  private fun getRestsGraph(): Array<Set<Int>> {
    val bounds = input.flatMap { listOf(it.start, it.end) }.bounds.second
    val space = MutableIntSpace(bounds.x + 1, bounds.y + 1, bounds.z + 1) { -1 }
    val sortedBricks = input.sortedBy { minOf(it.start.z, it.end.z) }
    val restsOn = Array<Set<Int>>(sortedBricks.size) { emptySet() }

    sortedBricks.forEachIndexed { index, brick ->
      var z = minOf(brick.start.z, brick.end.z)
      while (z > 1) {
        restsOn[index] = brick.projection.map { space[Point3i(it.x, it.y, z - 1)] }.filter { it != -1 }.toSet()
        if (restsOn[index].isNotEmpty()) {
          // existing brick breaks the fall
          break
        }
        z--
      }
      val zoff = z - minOf(brick.start.z, brick.end.z)
      // place brick
      for (p in brick.units) {
        space[p + Point3i(0, 0, zoff)] = index
      }
    }

    return restsOn
  }
}
