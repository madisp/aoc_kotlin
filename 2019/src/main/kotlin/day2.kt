import utils.Parser
import utils.Solution

fun main() {
  Day2.run()
}

enum class OpCode(val code: Int) {
  ADD(1), MUL(2), HLT(99)
}

object Day2 : Solution<List<Int>>() {
  override val name = "day2"
  override val parser: Parser<List<Int>> = Parser.ints

  private fun compute(input: List<Int>, init1: Int, init2: Int): Int {
    var ip = 0
    val mem = input.toMutableList()

    mem[1] = init1
    mem[2] = init2

    while (true) {
      when (OpCode.entries.find { it.code == mem[ip] }) {
        OpCode.ADD -> {
          mem[mem[ip + 3]] = mem[mem[ip + 1]] + mem[mem[ip + 2]]
        }
        OpCode.MUL -> {
          mem[mem[ip + 3]] = mem[mem[ip + 1]] * mem[mem[ip + 2]]
        }
        OpCode.HLT -> {
          break
        }
        null -> throw IllegalStateException("Unknown opcode ${mem[ip]} at $ip")
      }
      ip += 4
    }

    return mem[0]
  }

  override fun part1(input: List<Int>): Int {
    return compute(input, 12, 2)
  }

  override fun part2(input: List<Int>): Int {
    val search = 100
    (0 until search).forEach { x ->
      (0 until search).forEach { y ->
        if (compute(input, x, y) == 19690720) {
          return x * 100 + y
        }
      }
    }

    throw IllegalArgumentException("No input satisfies condition (== 19690720)")
  }
}
