import utils.IntGrid
import utils.Parser
import utils.Solution

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

object Day4Fast : Solution<Pair<List<Int>, List<IntGrid>>>() {
  override val name = "day4"
  override val parser = Parser.compoundList(Parser.ints, IntGrid.table)

  override fun part1(input: Pair<List<Int>, List<IntGrid>>): Int {
    val (numbers, boards) = input
    return solve(numbers, boards).first
  }

  override fun part2(input: Pair<List<Int>, List<IntGrid>>): Int {
    val (numbers, boards) = input
    return solve(numbers, boards).second
  }

  // given a list of numbers and a list of boards returns the score of the first winner and the last winner
  private fun solve(numbers: List<Int>, boards: List<IntGrid>): Pair<Int, Int> {
    // build a lookup table of number -> position
    val numberPos = IntArray(numbers.maxOrNull()!! + 1)
    numbers.forEachIndexed { index, number -> numberPos[number] = index }

    // round index to board, we'll use this to get values
    var first = Integer.MAX_VALUE to IntGrid.EMPTY
    var last = Integer.MIN_VALUE to IntGrid.EMPTY

    for (board in boards) {
      var winningRound = Integer.MAX_VALUE
      for (x in 0 until 5) {
        var rowMax = Integer.MIN_VALUE
        var colMax = Integer.MIN_VALUE
        for (y in 0 until 5) {
          // x=row y=col
          val rowNum = numberPos[board[x][y]]
          if (rowNum > rowMax) rowMax = rowNum
          // abuse the same loop to check cols, x=col y=row
          val colNum = numberPos[board[y][x]]
          if (colNum > colMax) colMax = colNum
        }
        if (rowMax < winningRound) winningRound = rowMax
        if (colMax < winningRound) winningRound = colMax
      }
      if (winningRound < first.first) first = winningRound to board
      if (winningRound > last.first) last = winningRound to board
    }

    fun score(state: Pair<Int, IntGrid>): Int {
      val (round, board) = state
      var sum = 0
      for (num in board.values) {
        if (numberPos[num] > round) {
          sum += num
        }
      }
      return sum * numbers[round]
    }

    return score(first) to score(last)
  }
}
