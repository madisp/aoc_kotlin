import utils.Mat4i
import utils.Parser
import utils.Point3i
import utils.ROTS
import utils.Solution
import utils.Vec4i
import utils.product
import utils.productIndexed
import utils.withCounts

fun main() {
  Day19.run()
}

object Day19 : Solution<List<Day19.Scanner>>() {
  override val name = "day19"
  override val parser = Parser { input ->
    input.split("\n\n").map { scannerIn ->
      scannerIn.lines().filter { it.isNotBlank() && !it.startsWith("---") }
        .map { line ->
          val (x, y, z) = line.split(",").map { it.toInt() }
          Point3i(x, y, z)
        }
    }.mapIndexed { index, beacons -> Scanner(index, beacons) }
  }

  class Scanner(val index: Int, val beacons: List<Vec4i>, distances: Map<Int, Pair<Int, Int>>? = null) {
    override fun toString() = "$index"

    private val distances = distances ?: beacons.productIndexed { b1Index, b1, b2Index, b2 ->
      b1.distanceSqr(b2) to (b1Index to b2Index)
    }.groupBy { it.first }.mapValues { (_, v) ->
      v.also { require(it.size == 1) { "Unexpected dupe distance in beacons" } }.first().second
    }

    fun translateTo(anchor: Scanner): Pair<Scanner, Vec4i>? {
      val distanceOverlap = this.distances.keys intersect anchor.distances.keys

      if (distanceOverlap.size < 12) {
        return null
      }

      // we've got at least 12 similar distances, try to map beacons now.

      // map every point to 24 possible transform matrices and find the
      // transform matrix that gave the most overlapping beacons
      val matPair = distanceOverlap.flatMap { dist ->
        val (myB1, myB2) = this.distances[dist]!!
        val (anchorB1, anchorB2) = anchor.distances[dist]!!

        ROTS.mapNotNull { rot ->
          // try to rotate the beacons in the distance pair
          val rotB1 = rot * beacons[myB1]
          val rotB2 = rot * beacons[myB2]

          // the delta between these pairs should be the same now
          // TODO(madis) is there a bug here as we're not considering rotB1 vs anchorB2 and vice versa?
          if (rotB1 - anchor.beacons[anchorB1] == rotB2 - anchor.beacons[anchorB2]) {
            val translation = Mat4i.translate(anchor.beacons[anchorB1] - rotB1)
            (translation * rot)
          } else if (rotB2 - anchor.beacons[anchorB1] == rotB1 - anchor.beacons[anchorB2]) {
            val translation = Mat4i.translate(anchor.beacons[anchorB2] - rotB1)
            (translation * rot)
          } else null
        }
      }.withCounts().maxByOrNull { it.value }

      // discard matrices that didn't yield at least 12 overlapping beacons
      if (matPair == null || matPair.value < 12) {
        return null
      }

      // transform the scanner with our transform matrix and also transform 0,0,0 to get scanner's position relative to
      // the anchor
      return Scanner(index, beacons.map { (matPair.key * it).asPoint }, distances) to matPair.key * Point3i(0,0,0)
    }
  }

  private fun solve(input: List<Scanner>): List<Pair<Scanner, Vec4i>> {
    val anchored = mutableListOf(input.first() to Point3i(0, 0, 0))
    val scanners = input.drop(1).toMutableList()

    while (scanners.isNotEmpty()) {
      val beginSz = scanners.size

      // iterate over scanners and try to anchor them
      val iter = scanners.listIterator()
      while (iter.hasNext()) {
        val scanner = iter.next()
        val matches = anchored.firstNotNullOfOrNull { (anchor, _) -> scanner.translateTo(anchor) }
        if (matches != null) {
          anchored.add(matches)
          iter.remove()
        }
      }

      require(scanners.size != beginSz) { "Couldn't anchor any scanner!" }
    }

    return anchored
  }

  override fun part1(input: List<Scanner>): Int {
    return solve(input).flatMap { (scanner, _) -> scanner.beacons }.toSet().size
  }

  override fun part2(input: List<Scanner>): Number? {
    return solve(input).map { (_, pos) -> pos }.product().maxOfOrNull { (pos1, pos2) -> pos1.distanceManhattan(pos2) }
  }
}
