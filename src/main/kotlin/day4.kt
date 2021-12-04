import kotlin.system.measureNanoTime

fun main() {
  val lines = readFile("day4").lines().filter { it.isNotBlank() }

  if (lines.size % 5 != 1) {
    throw IllegalArgumentException("Input should have X*5+1 lines")
  }

  val numbers = lines.first().split(',').map { it.toInt() }
  val boards = lines.subList(1, lines.size)
    .chunked(5, Board::fromLines)

  // functional approach
  val functionalTime = measureNanoTime {
    val score1 = numbers.indices.asSequence()
      .map { count ->
        numbers.take(count).toSet() to boards
      }
      .flatMap { (numbers, boards) ->
        boards.map { numbers to it }
      }
      .first { (numbers, board) -> board.winning(numbers) }
      .let { (numbers, board) -> board.score(numbers) }

    println(score1)

    val score2 = numbers.indices.asSequence()
      .map { count ->
        numbers.take(count).toSet() to boards
      }
      .flatMap { (numbers, boards) ->
        boards.map { numbers to it }
      }
      .last { (numbers, board) ->
        board.winning(numbers) && !board.winning(
          numbers - numbers.last()
        )
      }
      .let { (numbers, board) -> board.score(numbers) }

    println(score2)
  }

  // imperative approach
  val imperativeTime = measureNanoTime {
    val takenNumbers = mutableSetOf<Int>()
    val boardsList = mutableListOf(*boards.toTypedArray())
    val winningBoards = mutableListOf<Triple<Int, Int, Board>>()
    numbers.forEachIndexed { index, number ->
      takenNumbers.add(number)
      val iterator = boardsList.listIterator()
      while (iterator.hasNext()) {
        val board = iterator.next()
        if (board.winning(takenNumbers)) {
          iterator.remove()
          winningBoards.add(Triple(index + 1, board.score(takenNumbers), board))
        }
      }
    }

    println(winningBoards.minByOrNull { it.first }?.second)
    println(winningBoards.maxByOrNull { it.first }?.second)
  }

  // faaaaaaast, single pass over every board plus score calculation
  val faaaaaaastTime = measureNanoTime {
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

    fun fastScore(state: Pair<Int, Board>): Int {
      val (round, board) = state
      var sum = 0
      for (num in board.nums) {
        if (numberPos[num] > round) {
          sum += num
        }
      }
      return sum * numbers[round]
    }
    println(fastScore(first))
    println(fastScore(last))
  }

  println("functional took ${functionalTime}ns")
  println("imperative took ${imperativeTime}ns")
  println("faaaaaaast took ${faaaaaaastTime}ns")
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

  fun rowWinning(row: Int, marked: Set<Int>): Boolean {
    return nums.subList(row * 5, row * 5 + 5).all { it in marked }
  }

  fun colWinning(col: Int, marked: Set<Int>): Boolean {
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
