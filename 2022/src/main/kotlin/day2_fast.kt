import utils.Parser
import utils.Solution

fun main() {
  Day2Fast.run()
}

object Day2Fast : Solution<LongArray>() {
  override val name = "day2"

  // packed longs of plays, where a nibble in each long is
  // a game with the bit layout of RRLL
  override val parser = Parser.lines.map {
    it.chunked(16).map { lines ->
      var packed = 0L
      lines.forEachIndexed { i, line ->
        val play = ((line[0] - 'A' + ((line[2] - 'X') shl 2)) + 1).toLong()
        packed = packed or (play shl (i shl 2))
      }
      packed
    }.toLongArray()
  }

  override fun part1(input: LongArray): Long {
    // packed nibbles of scores
    val lookup = 0x69302580714 shl 4
    var score = 0L
    input.forEach { packed ->
      (0 until 16).forEach { idx ->
        val play = (packed ushr (idx shl 2)).toInt() and 15
        score += (lookup ushr (play shl 2)) and 15
      }
    }
    return score
  }

  override fun part2(input: LongArray): Long {
    // packed nibbles of scores
    val lookup = 0x79806540213 shl 4
    var score = 0L
    input.forEach { packed ->
//      (0 until 16).forEach { idx ->
//        val play = (packed ushr (idx shl 2)).toInt() and 15
//        score += (lookup ushr (play shl 2)) and 15
//      }
      // truly heinous unrolled loop. original rolled one above
      score += (((lookup ushr ((packed.toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 4).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 8).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 12).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 16).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 20).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 24).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 28).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 32).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 36).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 40).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 44).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 48).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 52).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 56).toInt() and 15) shl 2)) and 15)
        + ((lookup ushr (((packed ushr 60).toInt() and 15) shl 2)) and 15))
    }
    return score
  }
}
