package utils

import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * A solution:
 *
 * - provides a parser from String -> In
 * - turns input In into answer Number for two parts
 * - provides a helper [run] method to run and benchmark a solution
 */
abstract class Solution<In> {
  abstract val name: String
  abstract val parser: Parser<In>
  open fun part1(input: In): Number? = null
  open fun part2(input: In): Number? = null

  @OptIn(ExperimentalTime::class) fun run(
  header: String? = null,
  printParseTime: Boolean = true,
  skipTest: Boolean = false,
  skipPart1: Boolean = false
  ) {
    val parse = parser
    val (input, parseTime) = measureTimedValue {
      parse(readFile(name))
    }
    val part2Input = parse(readFile(name))

    if (!header.isNullOrBlank()) {
      println("==== $header ====")
    }

    if (printParseTime) {
      println("---- parsing ----")
      println("In $parseTime")
      println()
    }

    if (!skipTest) {
      val test1 = part1(parse(readFile("${name}_test")))
      val test2 = part2(parse(readFile("${name}_test")))
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
      val (firstAnswer, firstTime) = measureTimedValue {
        part1(input)
      }
      println("part1: $firstAnswer ($firstTime)")
    }

    val (secondAnswer, secondTime) = measureTimedValue {
      part2(part2Input)
    }
    val prefix = if (skipPart1) "" else "part2: "
    println("${prefix}${secondAnswer} ($secondTime)")

    println()
  }
}
