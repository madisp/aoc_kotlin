import utils.Parser
import utils.mapItems

fun main() {
  Day8Func.run()
}

object Day8Func : Solution<List<Day8Func.Key>> {
  override val name = "day8"
  override val parser = Parser.lines.mapItems {
    val (input, output) = it.split(" | ", limit = 2)
    Key(input.split(' '), output.split(' '))
  }

  override fun part1(keys: List<Key>): Int {
    return keys.flatMap {
      it.output.filter { out -> out.length in setOf(2, 3, 4, 7) }
    }.count()
  }

  override fun part2(input: List<Key>): Number? {
    return input.map {
      val p = solve(it.digits)
      it.output.map { A[remap(it, p)] }.joinToString(separator = "").toInt()
    }.sum()
  }

  fun permutations(input: String): List<String> {
    if (input.length == 1) {
      return listOf(input)
    }
    return input.toCharArray().flatMapIndexed { index, char ->
      permutations(input.substring(0 until index) + input.substring(index + 1)).map {
        char + it
      }
    }
  }

  val P = permutations("abcdefg")
  val A = mapOf(
    "abcefg" to '0',
    "cf" to '1',
    "acdeg" to '2',
    "acdfg" to '3',
    "bcdf" to '4',
    "abdfg" to '5',
    "abdefg" to '6',
    "acf" to '7',
    "abcdefg" to '8',
    "abcdfg" to '9'
  )

  fun remap(key: String, perm: String): String {
    return key.toCharArray().map {
      when (it) {
        'a' -> perm[0]
        'b' -> perm[1]
        'c' -> perm[2]
        'd' -> perm[3]
        'e' -> perm[4]
        'f' -> perm[5]
        'g' -> perm[6]
        else -> throw IllegalStateException("Bad input, bad!")
      }
    }.sorted().joinToString(separator = "")
  }

  fun solve(digits: List<String>): String {
    val solved = P.filter { p -> digits.map { remap(it, p) }.toSet() == A.keys }
    return solved.first()
  }

  data class Key(val digits: List<String>, val output: List<String>)
}
