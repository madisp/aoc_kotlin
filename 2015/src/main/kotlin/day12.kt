import utils.Parser
import utils.Solution

fun main() {
  Day12.run()
}

object Day12 : Solution<String>() {
  override val name = "day12"
  override val parser: Parser<String> = Parser { it }

  data class VisitResult(
    val sum: Int,
    val redSum: Int,
    val len: Int,
    val red: Boolean,
  )

  private fun visitArray(off: Int, str: String): VisitResult {
    var sum = 0
    var redSum = 0
    var o = off + 1
    while (str[o] != ']') {
      if (str[o] == ',') {
        o++
      }
      val (s, rs, l, _) = visitValue(o, str)
      o += l
      sum += s
      redSum += rs
    }
    return VisitResult(sum, redSum, o - off + 1, false)
  }

  private fun visitObj(off: Int, str: String): VisitResult {
    var o = off + 1
    var sum = 0
    var redSum = 0
    var red = false
    while (str[o] != '}') {
      if (str[o] == ',') {
        o++
      }
      // eat key
      val (_, _, l, _) = visitStr(o, str)
      o += l
      // eat ':'
      o += 1
      val value = visitValue(o, str)
      red = red || value.red
      sum += value.sum
      redSum += value.redSum
      o += value.len
    }

    return VisitResult(if (red) 0 else sum, if (red) sum + redSum else redSum, o - off + 1, false)
  }

  private fun visitStr(off: Int, str: String): VisitResult {
    val end = str.indexOf('"', startIndex = off + 1)

    return VisitResult(0, 0, end - off + 1, str.substring(off + 1, end) == "red")
  }

  private fun visitNum(off: Int, str: String): VisitResult {
    val endOfNumber = str.substring(off + 1).indexOfFirst { it !in '0'..'9' } + off + 1
    val num = str.substring(off, endOfNumber).toInt()
    return VisitResult(num, 0, endOfNumber - off, false)
  }

  private fun visitValue(off: Int, str: String): VisitResult {
    return when (str[off]) {
      '[' -> visitArray(off, str)
      '{' -> visitObj(off, str)
      '"' -> visitStr(off, str)
      else -> visitNum(off, str)
    }
  }

  override fun part1(input: String): Int {
    val result = visitValue(0, input)
    return result.sum + result.redSum
  }

  override fun part2(input: String): Int {
    val result = visitValue(0, input)
    return result.sum
  }
}
