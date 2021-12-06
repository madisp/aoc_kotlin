fun main() {
  Day4Func.run()
}

object Day4Func : Solution<Pair<List<Int>, List<Day4Func.Board>>> {
  override val name = "day4"
  override val parser = Parser.lines.map { lines ->
    val numbers = lines.first().split(',').map { it.toInt() }
    val boards = lines.asSequence().drop(1).chunked(5, Board::fromLines).toList()
    numbers to boards
  }

  override fun part1(input: Pair<List<Int>, List<Board>>): Int {
    val (numbers, boards) = input

    return numbers.indices.asSequence()
      .map { count -> numbers.take(count).toSet() to boards }
      .flatMap { (numbers, boards) -> boards.map { numbers to it } }
      .first { (numbers, board) -> board.winning(numbers) }
      .let { (numbers, board) -> board.score(numbers) }
  }

  override fun part2(input: Pair<List<Int>, List<Board>>): Number? {
    val (numbers, boards) = input

    return numbers.indices.asSequence()
      .map { count -> numbers.take(count).toSet() to boards }
      .flatMap { (numbers, boards) -> boards.map { numbers to it } }
      .last { (numbers, board) -> board.winning(numbers) && !board.winning(numbers - numbers.last()) }
      .let { (numbers, board) -> board.score(numbers) }
  }

  data class Board(val nums: List<Int>) {
    fun score(marked: Set<Int>): Int {
      return (nums.toSet() - marked).sum() * marked.last()
    }

    fun winning(marked: Set<Int>): Boolean {
      return (0 until 5).any {
        rowWinning(it, marked) || colWinning(it, marked)
      }
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
