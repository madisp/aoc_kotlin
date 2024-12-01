import utils.Parser
import utils.Solution

fun main() {
  Day4.run()
}

typealias Day4In = String

object Day4 : Solution<Day4In>() {
  override val name = "day4"
  override val parser: Parser<Day4In> = Parser { it.trim() }

  private const val HI = "ABCDEFGHIJKLMNOPRSŠZŽTUVÕÄÖÜ"
  private const val LO = "abcdefghijklmnoprsšzžtuvõäöü"

  val dictionary = listOf("kui", "ja", "siis", "on", "olen", "oled", "et", "ei", "mina", "sina", "ma", "sa", "mis", "ka", "või", "vä")

  private fun rot(c: Char, shift: Int, alphabet: String): Char {
    return alphabet[(alphabet.indexOf(c) + shift + alphabet.length) % alphabet.length]
  }

  override fun part1(input: Day4In): String {
    return HI.indices.map { shift ->
      input.toCharArray().map { char ->
        val alphabet = listOf(HI, LO).firstOrNull { char in it }
        if (alphabet == null) char else rot(char, shift, alphabet)
      }.joinToString("")
    }.first { dictionary.any { word -> " $word " in it.lowercase() } }
  }
}
