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
}
