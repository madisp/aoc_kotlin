import utils.*
import java.math.BigDecimal
import kotlin.math.absoluteValue

fun main() {
  Day21.run()
}

typealias Day21In = List<String>

object Day21 : Solution<Day21In>() {
  override val name = "day21"
  override val parser: Parser<Day21In> = Parser.lines

  val codeKeypad = Parser.charGrid("""
    789
    456
    123
    .0A
  """.trimIndent())

  val dirKeypad = Parser.charGrid("""
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

    if (start + Vec2i(delta.x, 0) in keypad && keypad[start + Vec2i(delta.x, 0)] == '.') {
      return listOf(vert + horiz)
    } else if (start + Vec2i(0, delta.y) in keypad && keypad[start + Vec2i(0, delta.y)] == '.') {
      return listOf(horiz + vert)
    } else {
      return listOf(horiz + vert, vert + horiz)
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

  fun shortestCode2(code: String, cycles: Int): BigDecimal {
    val memo = mutableMapOf<Triple<Int, Char, Char>, BigDecimal>()
    repeat(cycles) { cycle ->
      "^v<>A".forEach { a ->
        "^v<>A".forEach { b ->
          if (cycle == 0) {
            val move = move(dirKeypad, a, b).minBy { it.length } + "A"
            memo[Triple(cycle, a, b)] = move.length.toBigDecimal()
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
    val fullMoves = kpMoves.map { kpm ->
      "A$kpm".zipWithNext().sumOf {
        (a, b) -> memo[Triple(cycles - 1, a, b)]!!
      }
//      moves(dirKeypad, 'A', kpm).toSet().map { d1 ->
//        "A$d1".zipWithNext().sumOf {
//            (a, b) -> memo[Triple(cycles - 1, a, b)]!!
//        }
//      }
    }

    return fullMoves.min()
  }

  override fun part1(input: Day21In): BigDecimal {
    // test p1: 126384
    // answ p1: 188398
    return input.sumOf { code ->
      val c1 = shortestCode2(code, 2)
      val c2 = code.filter { it.isDigit() }.toInt(10).toBigDecimal()
      c1 * c2
    }
  }

  override fun part2(input: Day21In): BigDecimal {
    return input.sumOf { code ->
      val c1 = shortestCode2(code, 25)
//      println("$code - $c1")
      val c2 = code.filter { it.isDigit() }.toInt(10).toBigDecimal()
      c1 * c2
    }

    // 653963398181276 too high
    // 261252013494090 too high
    // 198187405225570 wrong
    // 104367628740070 too low

    // wrong: 132680405989994
    // 154115708116294

    // wrong: 198187405225570
    // correct: 230049027535970
  }
}
