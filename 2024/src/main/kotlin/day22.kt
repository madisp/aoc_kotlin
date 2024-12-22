import utils.Parser
import utils.Solution

fun main() {
  Day22.run()
}

typealias Day22In = List<Int>

object Day22 : Solution<Day22In>() {
  override val name = "day22"
  override val parser: Parser<Day22In> = Parser.intLines

  private fun calculate(prev: Long): Long {
    val mul = ((prev shl 6) xor prev) and 0xffffffL
    val div = ((mul ushr 5) xor mul) and 0xffffffL
    val mul2 = ((div shl 11) xor div) and 0xffffffL
    return mul2
  }

  override fun part1(input: Day22In): Long {
    return input.sumOf { deltas(it.toLong()).toList().last().first }
  }

  private fun deltas(seed: Long): Sequence<Pair<Long, Int>> {
    return generateSequence(seed to 0) { (prev, _) ->
      val next = calculate(prev)
      next to ((next % 10) - (prev % 10)).toInt()
    }.drop(1).take(2000)
  }

  data class Key(
    val x: Int,
    val y: Int,
    val z: Int,
    val w: Int,
  )

  override fun part2(input: Day22In): Int {
    val bananas = mutableMapOf<Pair<Int, Key>, Int>()

    input.forEach {
      deltas(it.toLong()).windowed(4).forEach { window ->
        val key = it to Key(window[0].second, window[1].second, window[2].second, window[3].second)
        if (key !in bananas) {
          bananas[key] = window[3].first.toInt() % 10
        }
      }
    }

    val sequences = (-9 .. 9).flatMap { x ->
      (-9 .. 9).flatMap { y ->
        (-9 .. 9).flatMap { z ->
          (-9 .. 9).map { w ->
            Key(x, y, z, w)
          }
        }
      }
    }
    return sequences.parallelStream().mapToInt { key ->
      input.sumOf {
        bananas[it to key] ?: 0
      }
    }.max().asInt
  }
}
