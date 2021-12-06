fun main() {
  Day4Imp.run()
}

object Day4Imp : Solution<Pair<List<Int>, List<Day4Imp.Board>>> {
  override val name = "day4"
  override val parser = Parser.lines.map { lines ->
    val numbers = lines.first().split(',').map { it.toInt() }
    val boards = lines.asSequence().drop(1).chunked(5, Board::fromLines).toList()
    numbers to boards
  }

  override fun part1(input: Pair<List<Int>, List<Board>>): Int {
    val (numbers, boards) = input
    return boardWinTimes(numbers, boards).minByOrNull { it.first }!!.second
  }

  override fun part2(input: Pair<List<Int>, List<Board>>): Int {
    val (numbers, boards) = input
    return boardWinTimes(numbers, boards).maxByOrNull { it.first }!!.second
  }

  // given a list of numbers and a list of boards calculates the turn win times and winning scores for each board
  private fun boardWinTimes(numbers: List<Int>, boards: List<Board>): List<Pair<Int, Int>> {
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

  data class Board(val nums: List<Int>) {
    fun score(marked: Set<Int>): Int {
      return (nums.toSet() - marked).sum() * marked.last()
    }

    fun winning(marked: Set<Int>): Boolean {
      (0 until 5).forEach { i ->
        if (rowWinning(i, marked) || colWinning(i, marked)) {
          return true
        }
      }
      return false
    }

    private fun rowWinning(row: Int, marked: Set<Int>): Boolean {
      return nums.subList(row * 5, row * 5 + 5).all { it in marked }
    }

    private fun colWinning(col: Int, marked: Set<Int>): Boolean {
      return (0 until 5).map { row -> nums[row * 5 + col] }.all { it in marked }
    }

    companion object {
      fun fromLines(lines: List<String>): Board {
        return Board(lines.flatMap { line ->
          line.split(' ').filter { it.isNotBlank() }.map { it.toInt() }
        })
      }
    }
  }
}
