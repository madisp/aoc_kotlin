package utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CollectionsTest {
  @Test
  fun testPermutations() {
    val list = listOf('A', 'B', 'C')
    val permutations = list.permutations.map { it.joinToString(separator = "") }.toList()

    assertThat(permutations).containsExactly(
      "ABC", "ACB", "BAC", "BCA", "CAB", "CBA"
    ).inOrder()
  }

  @Test
  fun testCombinations() {
    val list = listOf('A', 'B', 'C')
    val combinations = list.combinations.map { it.joinToString(separator = "") }.toList()

    assertThat(combinations).containsExactly(
      "", "C", "B", "BC", "A", "AC", "AB", "ABC"
    ).inOrder()
  }

  @Test
  fun testSelections1() {
    val list = listOf('A', 'B', 'C', 'D', 'E')
    val selections = list.selections(1).map { it.joinToString(separator = "") }.toList()

    assertThat(selections).containsExactly(
      "E", "D", "C", "B", "A"
    ).inOrder()
  }

  @Test
  fun testSelections2() {
    val list = listOf('A', 'B', 'C', 'D', 'E')
    val selections = list.selections(2).map { it.joinToString(separator = "") }.toList()

    assertThat(selections).containsExactly(
      "DE", "CE", "CD", "BE", "BD", "BC", "AE", "AD", "AC", "AB"
    ).inOrder()
  }

  @Test
  fun testSelections3() {
    val list = listOf('A', 'B', 'C', 'D', 'E')
    val selections = list.selections(3).map { it.joinToString(separator = "") }.toList()

    assertThat(selections).containsExactly(
      "CDE", "BDE", "BCE", "BCD", "ADE", "ACE", "ACD", "ABE", "ABD", "ABC"
    ).inOrder()
  }

  @Test(expected = IndexOutOfBoundsException::class)
  fun testMiddleOnEmptyThrows() {
    val list = emptyList<Int>()
    assertThat(list.middle).isNull()
  }

  @Test
  fun testMiddleOnSingletonList() {
    assertThat(listOf(1).middle).isEqualTo(1)
  }

  @Test
  fun testMiddleOnOddList() {
    assertThat(listOf(1,2,3).middle).isEqualTo(2)
  }

  @Test
  fun testMiddleOnEvenList() {
    // 2 or 3?
    assertThat(listOf(1,2,3,4).middle).isEqualTo(2)
  }
}
