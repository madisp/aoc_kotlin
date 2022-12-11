import utils.Parser
import utils.Solution
import utils.cut

fun main() {
  Day11Imp.run()
}

object Day11Imp : Solution<List<Day11Imp.Monkey>>() {
  override val name = "day11"
  override val parser = Parser { input ->
    input.split("\n\n").map { monkeyStr ->
      val monkeyLines = monkeyStr.split("\n")

      val items = monkeyLines[1].cut(":").second.trim().split(", ").map { it.trim().toLong() }

      val operation = monkeyLines[2].cut("=").second.trim()
      val (op1, oper, op2) = operation.split(" ")
      val expr = Expr(Operator.values().first { it.symbol == oper }, Operand.parse(op1), Operand.parse(op2))

      val divisibleBy = monkeyLines[3].split(" ").last().toLong()
      val ifTrue = monkeyLines[4].split(" ").last().toInt()
      val ifFalse = monkeyLines[5].split(" ").last().toInt()

      Monkey(
        items.toMutableList(),
        expr,
        Test(divisibleBy, ifTrue, ifFalse)
      )
    }
  }

  data class Monkey(
    val items: MutableList<Long>,
    val op: Expr,
    val test: Test
  )

  data class Test(
    val divisibleBy: Long,
    val throwToIfTrue: Int,
    val throwToIfFalse: Int,
  )

  data class Expr(
    val operator: Operator,
    val left: Operand,
    val right: Operand
  ) {
    operator fun invoke(old: Long): Long {
      val l = when (left) {
        Operand.Old -> old
        is Operand.Value -> left.v
      }
      val r = when (right) {
        Operand.Old -> old
        is Operand.Value -> right.v
      }
      return when (operator) {
        Operator.MULTIPLY -> l * r
        Operator.ADD -> l + r
      }
    }
  }

  enum class Operator(val symbol: String) {
    MULTIPLY("*"), ADD("+")
  }

  sealed class Operand {
    object Old : Operand()
    data class Value(val v: Long) : Operand()

    companion object {
      fun parse(input: String): Operand {
        if (input == "old") return Old
        return Value(input.toLong())
      }
    }
  }

  override fun part1(input: List<Monkey>): Long {
    return solve(input, 20, true)
  }

  override fun part2(input: List<Monkey>): Long {
    return solve(input, 10000, false)
  }

  private fun solve(input: List<Monkey>, repeats: Int, divByThree: Boolean): Long {
    val inspCounts = LongArray(input.size) { 0 }
    val clamp = input.map { it.test.divisibleBy }.reduce { a, b -> a * b }
    repeat(repeats) {
      input.forEachIndexed { monkeyIndex, monkey ->
        for (item in listOf(*monkey.items.toTypedArray())) {
          // inspect
          val newWorryLevel = (if (divByThree) monkey.op(item) / 3 else monkey.op(item)) % clamp
          val throwBool = (newWorryLevel % monkey.test.divisibleBy) == 0L
          val targetMonkey = if (throwBool) monkey.test.throwToIfTrue else monkey.test.throwToIfFalse
          input[targetMonkey].items.add(newWorryLevel)
          monkey.items.remove(item)
          inspCounts[monkeyIndex]++
        }
      }
    }

    inspCounts.sortDescending()
    return inspCounts[0] * inspCounts[1]
  }
}
