import utils.Parser
import utils.Solution
import utils.split

object Day3Fast : Solution<List<String>>() {
  override val name = "day3"
  override val parser = Parser.lines

  override fun part1(input: List<String>): Int {
    var sum = 0

    input.forEach { line ->
      val (l, r) = line.split()

      var lmask = 0L
      var rmask = 0L

      for (char in l.toCharArray()) {
        lmask = lmask or (1L shl char.priority)
      }
      for (char in r.toCharArray()) {
        rmask = rmask or (1L shl char.priority)
      }

      val priority = (lmask and rmask).countTrailingZeroBits()
      sum += (priority + 1)
    }

    return sum
  }

  override fun part2(input: List<String>): Number {
    var sum = 0

    (input.indices step 3).forEach { i ->
      var mask = 0L
      var linemask = 0L

      for (char in input[i].toCharArray()) { linemask = linemask or (1L shl char.priority) }
      mask = linemask
      linemask = 0
      for (char in input[i + 1].toCharArray()) { linemask = linemask or (1L shl char.priority) }
      mask = mask and linemask
      linemask = 0
      for (char in input[i + 2].toCharArray()) { linemask = linemask or (1L shl char.priority) }
      mask = mask and linemask

      sum += mask.countTrailingZeroBits() + 1
    }

    return sum
  }
}
