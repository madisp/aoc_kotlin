package year2025

import utils.Memo
import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day11.run()
}

typealias Day11In = Map<String, List<String>>

object Day11 : Solution<Day11In>() {
  override val name = "day11"
  override val parser: Parser<Day11In> = Parser.lines.mapItems { line ->
    val (serv, others) = line.cut(":")
    serv to others.split(" ")
  }.map {
    it.toMap()
  }

  data class Params(
    val start: String,
    val end: String,
  )

  private val countPaths get() = Memo<Params, Long> { (start, end) ->
    if (start == end) 1L
    else input[start]?.sumOf { this(Params(it, end)) } ?: 0L
  }

  private fun countPaths(vararg path: String): Long {
    val count = countPaths // share memo between invokes
    return path.toList().windowed(2).map {
      (a, b) -> count(Params(a, b))
    }.reduce { a, b -> a * b }
  }

  override fun part1(): Long {
    return countPaths("you", "out")
  }

  override fun part2(): Long {
    return countPaths("svr", "dac", "fft", "out") + countPaths("svr", "fft", "dac", "out")
  }
}
