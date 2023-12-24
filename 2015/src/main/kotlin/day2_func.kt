import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day2Func.run()
}

data class Box(val l: Int, val w: Int, val h: Int) {
  val area: Int get() = 2*l*w + 2*w*h + 2*h*l

  val slack: Int get() = minOf(l*w, w*h, h*l)

  val smallestPerimeter: Int get() = minOf(2*(l+w), 2*(w+h), 2*(h+l))

  val volume: Int get() = l * w * h
}

object Day2Func : Solution<List<Box>>() {
  override val name = "day2"
  override val parser = Parser.lines.mapItems { line ->
    val (l, w, h) = line.split('x', limit = 3).map { it.toInt() }
    Box(l, w, h)
  }

  override fun part1(): Int {
    return input.sumOf { it.area + it.slack}
  }

  override fun part2(): Number {
    return input.sumOf { it.smallestPerimeter + it.volume }
  }
}
