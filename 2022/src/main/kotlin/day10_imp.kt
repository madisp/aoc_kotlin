import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.cut
import kotlin.math.absoluteValue

fun main() {
  Day10Imp.run()
}

object Day10Imp : Solution<List<Day10Imp.Insn>>() {
  sealed class Insn {
    data class Add(val a: Int) : Insn()
    object Nop : Insn()
  }

  override val name = "day10"
  override val parser = Parser.lines.map { lines ->
    lines.flatMap {
      when (it.trim()) {
        "noop" -> listOf(Insn.Nop)
        else -> listOf(Insn.Nop, Insn.Add(it.cut(" ").second.toInt()))
      }
    }
  }

  override fun part1(input: List<Insn>): Int {
    var clock = 1
    var x = 1
    var answ = 0

    for (insn in input) {
      if (clock % 20 == 0) {
        if (clock == 20 || (clock - 20) % 40 == 0) {
          answ += clock * x
        }
      }
      if (insn is Insn.Add) {
        x += insn.a
      }
      clock += 1
    }
    return answ
  }

  override fun part2(input: List<Insn>): String {
    val screen = Grid(40, 6, 0).toMutable()
    var clock = 0
    var x = 1

    for (insn in input) {
      val px = Vec2i(clock % 40, clock / 40)
      if ((px.x - x).absoluteValue < 2) {
        screen[px] = 1
      }
      if (insn is Insn.Add) {
        x += insn.a
      }
      clock += 1
    }

    return screen.toString { _, v -> if (v == 1) "#" else " " }
  }
}
