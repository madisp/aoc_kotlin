package utils

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.TruthJUnit.assume
import org.junit.Test

abstract class SolutionTest<In : Any>(
  private val solution: Solution<In>,
  private val examples: Pair<Any?, Any?> = null to null,
  private val answers: Pair<Any?, Any?> = null to null,
) {
  val year = readFile("year").trim().toInt()
  val input = solution.parser(readInput(year, solution.day))
  val testInput get() = solution.parser(readFile("day${solution.day}_test"))

  @Test
  fun testPart1() {
    assume().that(examples.first).isNotNull()
    assertThat(solution.part1(testInput)).isEqualTo(examples.first)
  }

  @Test
  fun testPart2() {
    assume().that(examples.second).isNotNull()
    assertThat(solution.part2(testInput)).isEqualTo(examples.second)
  }

  @Test
  fun part1() {
    assume().that(answers.first).isNotNull()
    assertThat(solution.part1(input)).isEqualTo(answers.first)
  }

  @Test
  fun part2() {
    assume().that(answers.second).isNotNull()
    assertThat(solution.part2(input)).isEqualTo(answers.second)
  }
}
