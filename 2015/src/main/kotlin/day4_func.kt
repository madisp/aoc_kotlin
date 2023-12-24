import utils.Parser
import utils.Solution
import java.security.MessageDigest

fun main() {
  Day4Func.run()
}

object Day4Func : Solution<String>() {
  override val name = "day4"
  override val parser = Parser { it.trim() }

  private val md5 = MessageDigest.getInstance("MD5")

  override fun part1(): Int {
    return solve(input, "00000")
  }

  override fun part2(): Int {
    return solve(input, "000000")
  }

  private fun solve(input: String, prefix: String) = (1..Integer.MAX_VALUE).asSequence()
    .takeWhile { !hash("$input$it").startsWith(prefix) }
    .last() + 1

  fun hash(input: String): String {
    md5.reset()
    return md5.digest(input.toByteArray()).joinToString("") {
      it.toUByte().toString(16).padStart(2, '0')
    }
  }
}
