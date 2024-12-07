import utils.Parse
import utils.Parser
import utils.Solution
import utils.parseItems

fun main() {
  Day7.run()
}

typealias Day7In = List<EquationTemplate>

@Parse("{answ}: {r ' ' operands}")
data class EquationTemplate(
  val answ: Long,
  val operands: List<Long>
)

object Day7 : Solution<Day7In>() {
  override val name = "day7"
  override val parser: Parser<Day7In> = Parser.lines.parseItems { parseEquationTemplate(it) }

  enum class Op {
    ADD,
    MUL,
    CONCAT,
  }

  private fun isValid(eq: EquationTemplate, ops: List<Op>): Boolean {
    if (eq.operands.size == 1) {
      return eq.answ == eq.operands.first()
    }
    return ops.any { op ->
      when (op) {
        Op.ADD -> isValid(EquationTemplate(eq.answ - eq.operands.last(), eq.operands.dropLast(1)), ops)
        Op.MUL -> {
          if (eq.answ % eq.operands.last() != 0L) {
            false
          } else {
            isValid(EquationTemplate(eq.answ / eq.operands.last(), eq.operands.dropLast(1)), ops)
          }
        }
        Op.CONCAT -> {
          val answ = eq.answ.toString()
          val operand = eq.operands.last().toString()
          if (!answ.endsWith(operand)) {
            false
          } else {
            isValid(EquationTemplate(answ.removeSuffix(operand).toLong(), eq.operands.dropLast(1)), ops)
          }
        }
      }
    }
  }

  private fun solve(vararg validOps: Op): Long {
    return input.filter { isValid(it, validOps.toList()) }.sumOf { it.answ }
  }

  override fun part1(input: Day7In): Long = solve(Op.ADD, Op.MUL)
  override fun part2(input: Day7In): Long = solve(Op.ADD, Op.MUL, Op.CONCAT)
}
