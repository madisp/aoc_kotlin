import utils.*
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
  Day10.run()
}

typealias Day10In = Grid<Char>

object Day10 : Solution<Day10In>() {
  override val name = "day10"
  override val parser: Parser<Day10In> = Parser.charGrid

  fun los(grid: Grid<Char>, from: Vec2i, to: Vec2i): Boolean {
    val delta = to - from
    val div = gcd(delta.x.absoluteValue, delta.y.absoluteValue)
    val step = delta / div

    var current = from + step
    while (current != to) {
      if (grid[current] == '#') return false
      current += step
    }

    return true
  }

  override fun part1(input: Day10In): Any? {
    val roids = input.coords.filter { input[it] == '#' }
    return roids.maxOf { station ->
      roids.count {
        it != station && los(input, station, it)
      }
    }
  }

  // get the 360 degree angle from the y-axis
  val Vec2i.angle: Double get() {
    val angle = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()))
    return if (angle < 0) angle + 360 else angle
  }

  override fun part2(input: Day10In): Int {
    val g = input.toMutable()
    val roids = input.coords.filter { input[it] == '#' }
    val station = roids.maxBy { station ->
      roids.count {
        it != station && los(input, station, it)
      }
    }

    var vaporized = 0
    while (true) {
      val visible = roids.filter { it != station && los(g, station, it) }
      if (vaporized + visible.size >= 200) {
        val sorted = visible.sortedBy { (it - station).rotateCcw().angle }
        val lastToVaporize = sorted[199 - vaporized]
        return lastToVaporize.x * 100 + lastToVaporize.y
      }
      vaporized += visible.size
    }
  }
}
