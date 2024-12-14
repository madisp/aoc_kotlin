import utils.Parse
import utils.Parser
import utils.Solution
import utils.Vec2l
import utils.mapItems
import java.io.ByteArrayOutputStream
import java.util.BitSet
import java.util.zip.GZIPOutputStream
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

fun main() {
  Day14.run()
}

typealias Day14In = List<Day14.Robot>

object Day14 : Solution<Day14In>() {
  override val name = "day14"
  override val parser: Parser<Day14In> = Parser.lines
    .mapItems { parseRobotstr(it) }
    .mapItems { Robot(Vec2l(it.px, it.py), Vec2l(it.vx, it.vy)) }

  data class Robot(val p: Vec2l, val v: Vec2l)
  @Parse("p={px},{py} v={vx},{vy}")
  data class Robotstr(val px: Long, val py: Long, val vx: Long, val vy: Long)

  private fun step(robots: List<Robot>, dimen: Vec2l, steps: Long): List<Robot> {
    return robots.map { it.copy(p = (it.p + it.v * steps) % dimen) }
  }

  override fun part1(input: Day14In): Int {
    val dimen = if (input.size < 20) Vec2l(11, 7) else Vec2l(101, 103)
    // normalize input velocities to pos
    val normalized = input.map {
      it.copy(v = (it.v + dimen) % dimen)
    }
    val newPos = step(normalized, dimen, 100)

    val qSz = Vec2l(dimen.x / 2, dimen.y / 2)
    val qOffs = listOf(
      Vec2l(0, 0), Vec2l(dimen.x / 2 + 1, 0), Vec2l(0, dimen.y / 2 + 1), Vec2l(dimen.x / 2 + 1, dimen.y / 2 + 1)
    )

    val muls = qOffs.map { qOff ->
      newPos.count {
        val pos = it.p - qOff
        pos.x in 0 until qSz.x && pos.y in 0 until qSz.y
      }
    }

    return muls.fold(1) { acc, it -> acc * it }
  }

  private fun entropy(robots: List<Robot>, dimen: Vec2l): Double {
    val bits = BitSet(dimen.x.toInt() * dimen.y.toInt())
    robots.forEach { (p, _) ->
      bits.set((p.y * dimen.x + p.x).toInt())
    }
    val arr = bits.toByteArray()

    // compress
    val compressed = ByteArrayOutputStream().use { outBytes ->
      GZIPOutputStream(outBytes).use { gz ->
        gz.write(arr)
      }
      outBytes.size()
    }

    return compressed / arr.size.toDouble()
  }

  override fun part2(input: Day14In): Long? {
    if (input.size < 20) {
      return null
    }

    val dimen = Vec2l(101, 103)
    // normalize input velocities to pos
    val normalized = input.map {
      it.copy(v = (it.v + dimen) % dimen)
    }

    // establish a base level of entropy
    val randomPicks = generateSequence { Random.nextLong(10000L) }.take(1000).map {
      entropy(step(normalized, dimen, it), dimen)
    }.toList()

    val avg = randomPicks.average()
    val stddev = sqrt(randomPicks.map { (it - avg).pow(2) }.average())

    // halt if we're off by more than 9 stddevs
    val tree = (1 until 1000000L).find {
      entropy(step(normalized, dimen, it), dimen) < (avg - 9 * stddev)
    }

    return tree
  }
}
