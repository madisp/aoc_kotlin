import utils.Parser
import java.math.BigDecimal

fun main() {
  Day10.run()
}

object Day10 : Solution<List<String>> {
  override val name = "day10"
  override val parser = Parser.lines

  fun validate(str: String): Char? {
    val stack = ArrayDeque<Char>()
    for (char in str) {
      when (char) {
        '(', '[', '{', '<' -> stack.add(char)
        else -> {
          if (stack.removeLast() != char.opening) {
            return char
          }
        }
      }
    }

    // we still might have chars in the stack here

    return null
  }

  fun complete(str: String): BigDecimal {
    val stack = ArrayDeque<Char>()
    for (char in str) {
      when (char) {
        '(', '[', '{', '<' -> stack.add(char)
        else -> {
          if (stack.removeLast() != char.opening) {
            return BigDecimal(0)
          }
        }
      }
    }

    var completionPts = BigDecimal(0)
    while (stack.isNotEmpty()) {
      val char = stack.removeLast()
      completionPts *= BigDecimal(5)
      completionPts += BigDecimal(char.completionPts)
    }
    return completionPts
  }

  val Char.opening: Char get() =
    when (this) {
      ')' -> '('
      ']' -> '['
      '}' -> '{'
      '>' -> '<'
      else -> 'a'
    }

  val Char.score: Int get() =
    when (this) {
      ')' -> 3
      ']' -> 57
      '}' -> 1197
      '>' -> 25137
      else -> 0
    }

  val Char.completionPts: Int get() =
    when (this) {
      '(' -> 1
      '[' -> 2
      '{' -> 3
      '<' -> 4
      else -> 0
    }

  override fun part1(input: List<String>): Int {
    return input.mapNotNull { validate(it) }.sumOf { it.score }
  }

  override fun part2(input: List<String>): BigDecimal {
    val nums = input.map { complete(it) }.filter { it > BigDecimal(0) }.sorted()

    return nums[nums.size / 2]
  }
}
