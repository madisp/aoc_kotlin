import utils.MutableIntSpace
import utils.Parse
import utils.Parser
import utils.Point3i
import utils.Solution
import utils.Vec4i
import utils.mapItems

fun main() {
  Day22.run()
}

object Day22 : Solution<List<Day22.Brick>>() {
  override val name = "day22"
  override val parser = Parser.lines.mapItems { parseBrick(it) }

  @Parse("{start}~{end}")
  data class Brick(
    val start: Vec4i,
    val end: Vec4i,
  )

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
    val bounds = input.flatMap { listOf(it.start, it.end) }.reduce { a, b ->
      Point3i(
        x = maxOf(a.x, b.x),
        y = maxOf(a.y, b.y),
        z = maxOf(a.z, b.z),
      )
      }
    val space = MutableIntSpace(bounds.x + 1, bounds.y + 1, bounds.z + 1) { -1 }
    val sortedBricks = input.sortedBy { minOf(it.start.z, it.end.z) }
    val brickZoffsets = IntArray(sortedBricks.size) { -1 }
    val restsOn = Array<Set<Int>>(sortedBricks.size) { emptySet() }

    sortedBricks.forEachIndexed { index, brick ->
      var z = minOf(brick.start.z, brick.end.z)
      fall@ while (true) {
        if (z == 1) {
          // already at rest
          break
        }
        val brickSupports = mutableSetOf<Int>()
        for (x in minOf(brick.start.x, brick.end.x)..maxOf(brick.start.x, brick.end.x)) {
          for (y in minOf(brick.start.y, brick.end.y)..maxOf(brick.start.y, brick.end.y)) {
            if (space[Point3i(x, y, z - 1)] != -1) {
              brickSupports.add(space[Point3i(x, y, z - 1)])
            }
          }
        }
        restsOn[index] = brickSupports
        if (brickSupports.isNotEmpty()) {
          // existing brick breaks the fall
          break@fall
        }
        z--
      }
      val zoff = z - minOf(brick.start.z, brick.end.z)
      brickZoffsets[index] = zoff
      // place brick
      for (bx in minOf(brick.start.x, brick.end.x)..maxOf(brick.start.x, brick.end.x)) {
        for (by in minOf(brick.start.y, brick.end.y)..maxOf(brick.start.y, brick.end.y)) {
          for (bz in minOf(brick.start.z, brick.end.z)..maxOf(brick.start.z, brick.end.z)) {
            space[Point3i(bx, by, bz + zoff)] = index
          }
        }
      }
    }

    return restsOn
  }
}
