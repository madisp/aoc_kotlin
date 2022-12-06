import utils.GGrid
import utils.Parser
import utils.Solution
import utils.badInput
import utils.mapItems

fun main() {
  Day5Imp.run()
}

typealias Day5Input = Pair<List<MutableList<Char>>, List<Insn>>

val stackParser: Parser<List<MutableList<Char>>> = GGrid.chars().map { grid ->
  val stackCount = (grid.width + 1) / 4
  List(stackCount) { i ->
    grid[1 + i * 4].values
      .filter { !it.isWhitespace() }
      .dropLast(1)
      .reversed()
      .toMutableList()
  }
}

val insnParser = Parser.lines.mapItems(Insn.Companion::fromLine)

fun printStacks(stacks: List<List<Char>>) {
  val height = stacks.maxOf { it.size }
  for (i in (0 until height).reversed()) {
    println(stacks.joinToString(" ") { if (i >= it.size) "   " else "[${it[i]}]" })
  }
  println(stacks.indices.joinToString(" ") { (it + 1).toString().padStart(2, ' ').padEnd(3, ' ') })
}

data class Insn(
  val from: Int,
  val to: Int,
  val count: Int,
) {
  companion object {
    fun fromLine(line: String): Insn {
      val (count, from, to) = line.split("move", "from", "to")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.toInt() }
      return Insn(from - 1, to - 1, count)
    }
  }
}

object Day5Imp : Solution<Day5Input>() {
  override val name = "day5"
  override val parser = Parser.compound(stackParser, insnParser)

  override fun part1(input: Day5Input): Int {
    solve(input, false)
    return 0
  }

  override fun part2(input: Day5Input): Int {
    solve(input, true)
    return 0
  }

  private fun solve(input: Day5Input, crateMover9001: Boolean) {
    val (stacks, insns) = input
    for (insn in insns) {
      simulate(stacks, insn, crateMover9001)
    }

    val answ = buildString {
      for (stack in stacks) {
        append(stack.last())
      }
    }
    println(answ)
  }

  private fun simulate(stacks: List<MutableList<Char>>, insn: Insn, crateMover9001: Boolean) {
    if (stacks[insn.from].size < insn.count) {
      badInput()
    }
    val crane = stacks[insn.from].takeLast(insn.count).let {
      if (crateMover9001) it.reversed() else it
    }
    for (i in 0 until insn.count) {
      stacks[insn.from].removeLast()
    }
    stacks[insn.to].addAll(crane)
  }
}
