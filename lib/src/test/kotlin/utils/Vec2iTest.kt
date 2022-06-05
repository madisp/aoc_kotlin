package utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Vec2iTest {
  @Test fun testAdjacent() {
    val points = Vec2i(3, 8).adjacent

    assertThat(points).hasSize(4)
    assertThat(points).containsExactly(
      Vec2i(2, 8), // left
      Vec2i(4, 8), // right
      Vec2i(3, 7), // up
      Vec2i(3, 9), // down
    )
  }

  @Test fun testSurrounding() {
    val points = Vec2i(3, 8).surrounding

    assertThat(points).hasSize(8)

    assertThat(points).doesNotContain(Vec2i(3, 8))

    assertThat(points).containsExactly(
      Vec2i(2, 7), // top-left
      Vec2i(3, 7), // top
      Vec2i(4, 7), // top-right
      Vec2i(2, 8), // left
      Vec2i(4, 8), // right
      Vec2i(2, 9), // bottom-left
      Vec2i(3, 9), // bottom
      Vec2i(4, 9), // bottom-right
    )
  }
}