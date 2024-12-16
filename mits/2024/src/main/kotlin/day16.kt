import utils.*

fun main() {
  Day16.run()
}

typealias Day16In = List<String>

object Day16 : Solution<Day16In>() {
  override val name = "day16"
  override val parser: Parser<Day16In> = Parser.lines

  // minimal set of rules that works for my input
  fun syls(word: String): List<String> {
    val vowels = "aeiouõäöü"
    val syllables = mutableListOf<String>()
    val cur = StringBuilder()

    fun capture() {
      syllables.add(cur.toString())
      cur.clear()
    }

    cur.append(word.first())
    word.windowed(3).forEach { abc ->
      val (a, b, c) = abc.toCharArray()

      if (a in vowels && b !in vowels && c in vowels) {
        capture()
      } else if (a !in vowels && b !in vowels && c in vowels && cur.any { it in vowels }) {
        capture()
      }

      cur.append(b)

      if (a in vowels && b in vowels && c in vowels) {
        capture()
      }
    }
    cur.append(word.last())
    if (cur.isNotEmpty()) {
      syllables.add(cur.toString())
    }
    return syllables
  }

  override fun part1(input: Day16In): Int {
    return input.count { line -> line.split(" ").flatMap { syls(it) }.size != 16 }
  }
}
