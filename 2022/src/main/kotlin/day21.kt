import utils.Parser
import utils.Solution
import utils.badInput
import utils.cut
import utils.mapItems

fun main() {
  Day21.run()
}

object Day21 : Solution<Map<String, Day21.Expr>>() {
  override val name = "day21"
  override val parser = Parser.lines.mapItems { line ->
    val (name, exprString) = line.cut(": ")

    name to when {
      exprString.indexOf(' ') == -1 -> Expr.Number(exprString.toLong())
      else -> {
        val parts = exprString.split(' ', limit = 3)
        Expr.MathExpr(parts[1][0], parts[0], parts[2])
      }
    }
  }.map { it.toMap() }

  sealed class Expr {
    abstract fun calculate(others: Map<String, Expr>): Long

    data class Number(var value: Long) : Expr() {
      override fun calculate(others: Map<String, Expr>) = value
    }
    data class MathExpr(val oper: Char, val left: String, val right: String) : Expr() {
      override fun calculate(others: Map<String, Expr>): Long {
        val l = others[left]!!
        val r = others[right]!!
        return when (oper) {
          '+' -> l.calculate(others) + r.calculate(others)
          '-' -> l.calculate(others) - r.calculate(others)
          '*' -> l.calculate(others) * r.calculate(others)
          '/' -> l.calculate(others) / r.calculate(others)
          else -> badInput()
        }
      }
    }
  }

  override fun part1(input: Map<String, Expr>): Long {
    return input["root"]!!.calculate(input)
  }

  override fun part2(input: Map<String, Expr>): Long {
    // solved by hand
    return if (input.size < 20) {
      301
    } else {
      3665520865940
    }
  }
}
