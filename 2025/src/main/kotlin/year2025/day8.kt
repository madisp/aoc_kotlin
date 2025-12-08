package year2025

import utils.Parser
import utils.Solution
import utils.Vec4l
import utils.mapItems
import utils.times

fun main() {
  Day8.run()
}

typealias Day8In = List<Vec4l>

object Day8 : Solution<Day8In>() {
  override val name = "day8"
  override val parser: Parser<Day8In> = Parser.lines.mapItems {
    val (x, y, z) = it.split(",")
    Vec4l(x.toLong(), y.toLong(), z.toLong(), 1L)
  }

  class Context(size: Int) {
    val circuit = IntArray(size) { it }
    val lookup = buildMap {
      (0 until input.size).forEach { i -> put(i, mutableSetOf(i)) }
    }.toMutableMap()

    fun connect(ca: Int, cb: Int) {
      // if already same circuit, nothing to do
      if (ca == cb) return
      // add all of b into a
      lookup[ca]!!.addAll(lookup[cb]!!)
      lookup[cb]!!.forEach {
        circuit[it] = ca
      }
      lookup.remove(cb)
    }
  }

  fun pairs(input: Day8In): List<Pair<Int, Int>> {
    val idx = input.indices.toList()
    return (idx * idx)
      .filter { (a, b) -> a < b }
      .sortedBy { (a, b) -> input[a].distanceSqr(input[b]) }
  }

  override fun part1(input: Day8In): Long {
    val connections = if (input.size == 20) 10 else 1000
    val ctx = Context(input.size)
    pairs(input).take(connections).forEach { (a, b) ->
      ctx.connect(ctx.circuit[a], ctx.circuit[b])
    }
    return ctx.lookup.entries.map { (_, vs) -> vs.size.toLong() }.sortedDescending().take(3)
      .reduce { a, b -> a * b }
  }

  override fun part2(input: Day8In): Long {
    val ctx = Context(input.size)

    for ((a, b) in pairs(input)) {
      ctx.connect(ctx.circuit[a], ctx.circuit[b])
      if (ctx.lookup.size == 1) {
        return input[a].x * input[b].x
      }
    }

    throw IllegalStateException("No circuit?")
  }
}
