import utils.Parser
import utils.Solution

fun main() {
  Day11.run()
}

object Day11 : Solution<String>() {
  override val name = "day11"
  override val parser: Parser<String> = Parser { it.trim() }

  fun validPwd(pwd: String): Boolean {
    if ('i' in pwd || 'o' in pwd || 'l' in pwd) {
      return false
    }

    if (pwd.windowed(2).filter { it[0] == it[1] }.toSet().size < 2) {
      return false
    }

    if (pwd.windowed(3).none { it[0] + 1 == it[1] && it[1] + 1 == it[2] }) {
      return false
    }

    return true
  }

  fun next(pwd: String): String? {
    val char = pwd.indexOfLast { it != 'z' }
    if (char == -1) {
      return null
    }

    val zc = pwd.length - char - 1

    return pwd.substring(0, char) + (pwd[char] + 1) + "a".repeat(zc)
  }

  override fun part1(input: String): String {
    return generateSequence(input) { next(it) }
      .filter { validPwd(it) }
      .first()
  }

  override fun part2(input: String): String {
    return generateSequence(input) { next(it) }
      .filter { validPwd(it) }
      .drop(1)
      .first()
  }
}
