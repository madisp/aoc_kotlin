package utils

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.TruthJUnit.assume
import org.junit.Before
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
    solution.input = testInput
    assertThat(solution.part1()).isEqualTo(examples.first)
  }

  @Test
  fun testPart2() {
    assume().that(examples.second).isNotNull()
    solution.input = testInput
    assertThat(solution.part2()).isEqualTo(examples.second)
  }

  @Test
  fun part1() {
    assume().that(answers.first).isNotNull()
    solution.input = input
    assertThat(solution.part1()).isEqualTo(answers.first)
  }

  @Test
  fun part2() {
    assume().that(answers.second).isNotNull()
    solution.input = input
    assertThat(solution.part2()).isEqualTo(answers.second)
  }
}
