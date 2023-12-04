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
}
