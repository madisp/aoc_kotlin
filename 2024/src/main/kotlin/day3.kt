import utils.Parser
import utils.Solution

fun main() {
  Day3.run()
}

typealias Day3In = List<Insn>

private val insnParser = Regex("""(mul|do|don't)\(((\d+),(\d+))?\)""")

sealed interface Insn {
  data class Mul(val a: Int, val b: Int) : Insn
  data object Disable : Insn
  data object Enable : Insn
}

object Day3 : Solution<Day3In>() {
  override val name = "day3"
  override val parser: Parser<Day3In> = Parser { input ->
    insnParser.findAll(input).toList().map { match ->
      val (_, insn) = match.groupValues
      when (insn) {
        "do" -> Insn.Enable
        "don't" -> Insn.Disable
        "mul" -> {
          val (_, _, _, a, b) = match.groupValues
          Insn.Mul(a.toInt(), b.toInt())
        }
        else -> throw IllegalStateException("Unknown insn $insn")
      }
    }
  }

  override fun part1(input: Day3In): Int {
    return input.filterIsInstance<Insn.Mul>().sumOf { (a, b) -> a * b }
  }

  override fun part2(input: Day3In): Int {
    data class State(val sum: Int, val enabled: Boolean)

    return input.fold(State(0, true)) { regs, insn ->
      when (insn) {
        Insn.Disable -> regs.copy(enabled = false)
        Insn.Enable -> regs.copy(enabled = true)
        is Insn.Mul -> if (regs.enabled) {
          regs.copy(sum = regs.sum + (insn.a * insn.b))
        } else regs
      }
    }.sum
  }
}
