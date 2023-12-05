package utils

import java.util.Locale
import kotlin.time.measureTimedValue

/**
 * A solution:
 *
 * - provides a parser from String -> In
 * - turns input In into answer Number for two parts
 * - provides a helper [run] method to run and benchmark a solution
 */
abstract class Solution<In : Any> {
  abstract val name: String
  abstract val parser: Parser<In>
  open fun part1(input: In): Any? = null
  open fun part2(input: In): Any? = null
  lateinit var input: In

  fun run(
    header: String? = null,
    printParseTime: Boolean = true,
    skipTest: Boolean = true,
    skipPart1: Boolean = false,
    skipPart2: Boolean = false,
  ) {
    val parse = parser
    val day = name.lowercase(Locale.US).removePrefix("day").split('_')[0].trimStart('0').trim().toInt()
    val year = readFile("year").trim().toInt()
    val (part1Input, parseTime) = measureTimedValue {
      parse(readInput(year, day))
    }
    val part2Input = parse(readInput(year, day))

    if (!header.isNullOrBlank()) {
      println("==== $header ====")
    }

    if (printParseTime) {
      println("---- parsing ----")
      println("In $parseTime")
      println()
    }

    val testInput = try { readFile("${name}_test") } catch (_: Throwable) { null }

    if (!skipTest && testInput != null) {
      input = parse(testInput)
      val test1 = part1(input).let {
        if (it is String) "\n$it\n" else it
      }
      input = parse(testInput)
      val test2 = part2(input).let {
        if (it is String) "\n$it\n" else it
      }
      println("---- test ----")
      if (skipPart1) {
        println("$test2")
      } else {
        println("part1: $test1")
        println("part2: $test2")
      }
      println()

      println("---- puzzle ----")
    }

    if (!skipPart1) {
      input = part1Input
      val (firstAnswer, firstTime) = measureTimedValue {
        part1(input)
      }
      if (firstAnswer is String) {
        println("part1 ($firstTime):")
        println(firstAnswer)
      } else {
        val prefix = if (skipPart2) "" else "part1: "
        println("${prefix}$firstAnswer ($firstTime)")
      }
    }

    if (!skipPart2) {
      input = part2Input
      val (secondAnswer, secondTime) = measureTimedValue {
        part2(input)
      }
      if (secondAnswer is String) {
        println("part2 ($secondTime):")
        println(secondAnswer)
      } else {
        val prefix = if (skipPart1) "" else "part2: "
        println("${prefix}${secondAnswer} ($secondTime)")
      }
    }

    println()
  }
}
