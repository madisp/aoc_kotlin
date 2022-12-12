import utils.IntGrid
import utils.Parser
import utils.Solution

fun main() {
  Day4Func.run()
}

object Day4Func : Solution<Pair<List<Int>, List<IntGrid>>>() {
  override val name = "day4"
  override val parser = Parser.compoundList(Parser.ints, IntGrid.table)

  override fun part1(input: Pair<List<Int>, List<IntGrid>>): Int {
    val (numbers, boards) = input

    return numbers.indices.asSequence()
      .map { count -> numbers.take(count).toSet() to boards }
      .flatMap { (numbers, boards) -> boards.map { numbers to it } }
      .first { (numbers, board) -> board.winning(numbers) }
      .let { (numbers, board) -> board.score(numbers) }
  }

  override fun part2(input: Pair<List<Int>, List<IntGrid>>): Number? {
    val (numbers, boards) = input

    return numbers.indices.asSequence()
      .map { count -> numbers.take(count).toSet() to boards }
      .flatMap { (numbers, boards) -> boards.map { numbers to it } }
      .last { (numbers, board) -> board.winning(numbers) && !board.winning(numbers - numbers.last()) }
      .let { (numbers, board) -> board.score(numbers) }
  }

  private fun IntGrid.winning(marked: Set<Int>): Boolean {
    return rows.any { row -> row.values.all { it in marked } } || columns.any { col -> col.values.all { it in marked } }
  }


  private fun IntGrid.score(marked: Set<Int>): Int {
    return (values.toSet() - marked).sum() * marked.last()
  }
}
