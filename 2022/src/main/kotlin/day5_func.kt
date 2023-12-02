import utils.Grid
import utils.Parse
import utils.Parser
import utils.Solution
import utils.badInput
import utils.mapItems

fun main() {
  Day5Func.run()
}

typealias Day5Input = Pair<List<List<Char>>, List<Insn>>

val stackParser: Parser<List<List<Char>>> = Grid.chars().map { grid ->
  val stackCount = (grid.width + 1) / 4
  List(stackCount) { i ->
    grid[1 + i * 4].values
      .filter { !it.isWhitespace() }
      .dropLast(1)
      .reversed()
  }
}

val insnParser = Parser.lines.mapItems(::parseInsn)
  .mapItems { (from, to, count) -> Insn(from - 1, to - 1, count) }

@Parse("move {count} from {from} to {to}")
data class Insn(
  val from: Int,
  val to: Int,
  val count: Int,
)

object Day5Func : Solution<Day5Input>() {
  override val name = "day5"
  override val parser = Parser.compound(stackParser, insnParser)

  override fun part1(input: Day5Input): String {
    return solve(input, false)
  }

  override fun part2(input: Day5Input): String {
    return solve(input, true)
  }

  private fun solve(input: Day5Input, crateMover9001: Boolean): String {
    val (stacks, insns) = input
    val stacked = insns.fold(stacks) { s, insn -> simulate(s, insn, crateMover9001) }

    return stacked.map { it.last() }.joinToString("")
  }

  private fun simulate(stacks: List<List<Char>>, insn: Insn, crateMover9001: Boolean): List<List<Char>> {
    if (stacks[insn.from].size < insn.count) {
      badInput()
    }
    val crane = stacks[insn.from].takeLast(insn.count).let {
      if (crateMover9001) it else it.reversed()
    }
    return stacks.mapIndexed { i, stack ->
      when (i) {
        insn.from -> stack.dropLast(insn.count)
        insn.to -> stack + crane
        else -> stack
      }
    }
  }
}
