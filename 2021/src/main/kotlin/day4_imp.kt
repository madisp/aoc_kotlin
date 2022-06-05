import utils.Grid
import utils.Parser

fun main() {
  Day4Imp.run()
}

object Day4Imp : Solution<Pair<List<Int>, List<Grid>>> {
  override val name = "day4"
  override val parser = Parser.compoundList(Parser.ints, Grid.table)

  override fun part1(input: Pair<List<Int>, List<Grid>>): Int {
    val (numbers, boards) = input
    return boardWinTimes(numbers, boards).minByOrNull { it.first }!!.second
  }

  override fun part2(input: Pair<List<Int>, List<Grid>>): Int {
    val (numbers, boards) = input
    return boardWinTimes(numbers, boards).maxByOrNull { it.first }!!.second
  }

  // given a list of numbers and a list of boards calculates the turn win times and winning scores for each board
  private fun boardWinTimes(numbers: List<Int>, boards: List<Grid>): List<Pair<Int, Int>> {
    val takenNumbers = mutableSetOf<Int>()
    val boardsList = mutableListOf(*boards.toTypedArray())
    val winningBoards = mutableListOf<Pair<Int, Int>>()
    numbers.forEachIndexed { index, number ->
      takenNumbers.add(number)
      val iterator = boardsList.listIterator()
      while (iterator.hasNext()) {
        val board = iterator.next()
        if (board.winning(takenNumbers)) {
          iterator.remove()
          winningBoards.add(index + 1 to board.score(takenNumbers))
        }
      }
    }

    return winningBoards
  }

  private fun Grid.winning(marked: Set<Int>): Boolean {
    return rows.any { row -> row.values.all { it in marked } } || columns.any { col -> col.values.all { it in marked } }
  }


  private fun Grid.score(marked: Set<Int>): Int {
    return (values.toSet() - marked).sum() * marked.last()
  }
}
