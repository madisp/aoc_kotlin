import utils.Parser

fun main() {
  Day4Fast.run()
}

object Day4All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day4Func, "imp" to Day4Imp, "fast" to Day4Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = true, skipTest = true, printParseTime = false)
    }
  }
}

object Day4Fast : Solution<Pair<List<Int>, List<Day4Fast.Board>>> {
  override val name = "day4"
  override val parser = Parser.lines.map { lines ->
    val numbers = lines.first().split(',').map { it.toInt() }
    val boards = lines.asSequence().drop(1).chunked(5, Board::fromLines).toList()
    numbers to boards
  }

  override fun part1(input: Pair<List<Int>, List<Board>>): Int {
    val (numbers, boards) = input
    return solve(numbers, boards).first
  }

  override fun part2(input: Pair<List<Int>, List<Board>>): Int {
    val (numbers, boards) = input
    return solve(numbers, boards).second
  }

  // given a list of numbers and a list of boards returns the score of the first winner and the last winner
  private fun solve(numbers: List<Int>, boards: List<Board>): Pair<Int, Int> {
    // build a lookup table of number -> position
    val numberPos = IntArray(numbers.maxOrNull()!! + 1)
    numbers.forEachIndexed { index, number -> numberPos[number] = index }

    // round index to board, we'll use this to get values
    var first = Integer.MAX_VALUE to Board(emptyList())
    var last = Integer.MIN_VALUE to Board(emptyList())

    for (board in boards) {
      var winningRound = Integer.MAX_VALUE
      for (x in 0 until 5) {
        var rowMax = Integer.MIN_VALUE
        var colMax = Integer.MIN_VALUE
        for (y in 0 until 5) {
          // x=row y=col
          val rowNum = numberPos[board.nums[x * 5 + y]]
          if (rowNum > rowMax) rowMax = rowNum
          // abuse the same loop to check cols, x=col y=row
          val colNum = numberPos[board.nums[y * 5 + x]]
          if (colNum > colMax) colMax = colNum
        }
        if (rowMax < winningRound) winningRound = rowMax
        if (colMax < winningRound) winningRound = colMax
      }
      if (winningRound < first.first) first = winningRound to board
      if (winningRound > last.first) last = winningRound to board
    }

    fun score(state: Pair<Int, Board>): Int {
      val (round, board) = state
      var sum = 0
      for (num in board.nums) {
        if (numberPos[num] > round) {
          sum += num
        }
      }
      return sum * numbers[round]
    }

    return score(first) to score(last)
  }

  data class Board(val nums: List<Int>) {
    companion object {
      fun fromLines(lines: List<String>): Board {
        return Board(lines.flatMap { line ->
          line.split(' ').filter { it.isNotBlank() }.map { it.toInt() }
        })
      }
    }
  }
}
