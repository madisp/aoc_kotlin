package utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CoordTest {
  @Test fun testAdjacent() {
    val points = Coord(3, 8).adjacent

    assertThat(points).hasSize(4)
    assertThat(points).containsExactly(
      Coord(2, 8), // left
      Coord(4, 8), // right
      Coord(3, 7), // up
      Coord(3, 9), // down
    )
  }

  @Test fun testSurrounding() {
    val points = Coord(3, 8).surrounding

    assertThat(points).hasSize(8)

    assertThat(points).doesNotContain(Coord(3, 8))

    assertThat(points).containsExactly(
      Coord(2, 7), // top-left
      Coord(3, 7), // top
      Coord(4, 7), // top-right
      Coord(2, 8), // left
      Coord(4, 8), // right
      Coord(2, 9), // bottom-left
      Coord(3, 9), // bottom
      Coord(4, 9), // bottom-right
    )
  }
}