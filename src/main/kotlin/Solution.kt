import utils.Parser
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * A solution:
 *
 * - provides a parser from String -> In
 * - turns input In into answer Number for two parts
 * - provides a helper [run] method to run and benchmark a solution
 */
interface Solution<In> {
  val name: String
  val parser: Parser<In>
  fun part1(input: In): Number? = null
  fun part2(input: In): Number? = null
}

@OptIn(ExperimentalTime::class) fun <T> Solution<T>.run(
  header: String? = null,
  printParseTime: Boolean = true,
  skipTest: Boolean = false,
  skipPart1: Boolean = false
) {
  val parse = parser
  val (input, parseTime) = measureTimedValue {
    parse(readFile(name))
  }

  if (!header.isNullOrBlank()) {
    println("==== $header ====")
  }

  if (printParseTime) {
    println("---- parsing ----")
    println("In $parseTime")
    println()
  }

  if (!skipTest) {
    val testInput = parse(readFile("${name}_test"))
    val test1 = part1(testInput)
    val test2 = part2(testInput)
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
    part2(input)
  }
  val prefix = if (skipPart1) "" else "part2: "
  println("${prefix}${secondAnswer} ($secondTime)")

  println()
}
