import utils.Parser
import utils.Solution
import java.security.MessageDigest

fun main() {
  Day5.run()
}

object Day5 : Solution<String>() {
  override val name = "day5"
  override val parser = Parser { it.trim() }

  private val md5 = MessageDigest.getInstance("MD5")
  fun hash(input: String): String {
    md5.reset()
    return md5.digest(input.toByteArray()).joinToString("") {
      it.toUByte().toString(16).padStart(2, '0')
    }
  }

  fun chars(string: String): Sequence<Char> {
    return sequence {
      var counter = 0
      while (true) {
        val h = hash(string + counter)
        if (h.startsWith("00000")) {
          yield(h[5])
        }
        counter++
      }
    }
  }

  fun chars2(string: String): Sequence<Pair<Char, Int>> {
    return sequence {
      var counter = 0
      while (true) {
        val h = hash(string + counter)
        if (h.startsWith("00000")) {
          if (h[5] in '0' .. '7') {
            yield(h[6] to h[5].digitToInt())
          }
        }
        counter++
      }
    }
  }

  override fun part1(): String {
    return chars(input).take(8).joinToString("")
  }

  override fun part2(): String {
    val pwd = CharArray(8) { '_' }
    return chars2(input).firstNotNullOf { (char, pos) ->
      if (pwd[pos] == '_') {
        pwd[pos] = char
      }
      if (pwd.none { it == '_' }) {
        pwd.joinToString("")
      } else null
    }
  }
}
