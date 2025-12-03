import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day2.run()
}

typealias Day2In = List<LongRange>

object Day2 : Solution<Day2In>() {
  override val name = "day2"
  override val parser: Parser<Day2In> = Parser { input -> input.split(",")
    .map { r -> r.cut("-") { it.toLong() } } }
    .mapItems { (start, end) -> start .. end }

  fun invalidIds(range: LongRange, divisor: Int): Sequence<Long> {
    return range.asSequence().map {
      it.toString()
    }.filter {
      it.length % divisor == 0
    }.filter {
      val partLen = it.length / divisor
      it.take(partLen).repeat(divisor) == it
    }.map {
      it.toLong()
    }
  }

  override fun part1(input: Day2In): Long {
    return input.flatMap { invalidIds(it, 2) }.sum()
  }

  override fun part2(input: Day2In): Long {
    return input.asSequence().flatMap { range ->
      (2 .. range.last.toString().length).flatMap {
        invalidIds(range, it)
      }
    }.toSet().sum()
  }
}
