import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import kotlin.math.absoluteValue

fun main() {
  Day21.run()
}

typealias Day21In = List<String>

object Day21 : Solution<Day21In>() {
  override val name = "day21"
  override val parser: Parser<Day21In> = Parser.lines

  private val codeKeypad = Parser.charGrid("""
    789
    456
    123
    .0A
  """.trimIndent())

  private val dirKeypad = Parser.charGrid("""
    .^A
    <v>
  """.trimIndent())

  private fun move(keypad: Grid<Char>, from: Char, to: Char): List<String> {
    val start = keypad.coords.first { keypad[it] == from }
    val end = keypad.coords.first { keypad[it] == to }
    val delta = end - start

    val horiz = if (delta.x <= 0) {
      "<".repeat(delta.x.absoluteValue)
    } else {
      ">".repeat(delta.x)
    }

    val vert = if (delta.y <= 0) {
      "^".repeat(delta.y.absoluteValue)
    } else {
      "v".repeat(delta.y)
    }

    return if (start + Vec2i(delta.x, 0) in keypad && keypad[start + Vec2i(delta.x, 0)] == '.') {
      listOf(vert + horiz)
    } else if (start + Vec2i(0, delta.y) in keypad && keypad[start + Vec2i(0, delta.y)] == '.') {
      listOf(horiz + vert)
    } else {
      listOf(horiz + vert, vert + horiz)
    }
  }

  private fun moves(pad: Grid<Char>, start: Char, btns: String): Set<String> {
    return (if (btns.isEmpty()) {
      listOf("")
    } else {
      val variants = move(pad, start, btns.first())
      moves(pad, btns.first(), btns.drop(1)).flatMap { m ->
        variants.map { it + "A" + m }
      }
    }).toSet()
  }

  private fun shortestcode(code: String, cycles: Int): Long {
    val memo = mutableMapOf<Triple<Int, Char, Char>, Long>()
    repeat(cycles) { cycle ->
      "^v<>A".forEach { a ->
        "^v<>A".forEach { b ->
          if (cycle == 0) {
            val move = move(dirKeypad, a, b).minBy { it.length } + "A"
            memo[Triple(cycle, a, b)] = move.length.toLong()
          } else {
            val moves = move(dirKeypad, a, b).map { it + "A" }
            memo[Triple(cycle, a, b)] = moves.minOf {
              "A$it".zipWithNext().sumOf {
                (a, b) -> memo[Triple(cycle - 1, a, b)]!!
              }
            }
          }
        }
      }
    }

    val kpMoves = moves(codeKeypad, 'A', code).toSet()
    return kpMoves.minOf { kpm ->
      "A$kpm".zipWithNext().sumOf {
        (a, b) -> memo[Triple(cycles - 1, a, b)]!!
      }
    }
  }

  override fun part1(input: Day21In): Long {
    return input.sumOf { code ->
      val c1 = shortestcode(code, 2)
      val c2 = code.filter { it.isDigit() }.toLong(10)
      c1 * c2
    }
  }

  override fun part2(input: Day21In): Long {
    return input.sumOf { code ->
      val c1 = shortestcode(code, 25)
      val c2 = code.filter { it.isDigit() }.toLong(10)
      c1 * c2
    }
  }
}
