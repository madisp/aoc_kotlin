package utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GridTest {
  @Test fun testParse() {
    val input = """
      123
      456
    """.trimIndent()

    val grid = Grid.singleDigits(input)

    assertThat(grid.width).isEqualTo(3)
    assertThat(grid.height).isEqualTo(2)

    // test some cells
    assertThat(grid.get(0, 0)).isEqualTo(1)
    assertThat(grid.get(1, 1)).isEqualTo(5)
    assertThat(grid.get(2, 1)).isEqualTo(6)
  }

  @Test fun testParse2() {
    val input = """
      12
      34
      56
    """.trimIndent()

    val grid = Grid.singleDigits(input)

    assertThat(grid.width).isEqualTo(2)
    assertThat(grid.height).isEqualTo(3)

    // test some cells
    assertThat(grid.get(0, 0)).isEqualTo(1)
    assertThat(grid.get(1, 1)).isEqualTo(4)
    assertThat(grid.get(1, 2)).isEqualTo(6)
  }

  @Test fun getByCoord() {
    val input = """
      1234
      5678
    """.trimIndent()

    val grid = Grid.singleDigits(input)

    assertThat(grid[Vec2i(0, 0)]).isEqualTo(1)
    assertThat(grid[Vec2i(3, 0)]).isEqualTo(4)
    assertThat(grid[Vec2i(0, 1)]).isEqualTo(5)
    assertThat(grid[Vec2i(3, 1)]).isEqualTo(8)
  }

  @Test fun getByColumn() {
    val input = """
      1234
      5678
    """.trimIndent()

    val grid = Grid.singleDigits(input)

    assertThat(grid[0][0]).isEqualTo(1)
    assertThat(grid[3][0]).isEqualTo(4)
    assertThat(grid[0][1]).isEqualTo(5)
    assertThat(grid[3][1]).isEqualTo(8)
  }

  @Test fun checkContains() {
    val input = """
      1234
      5678
    """.trimIndent()
    val grid = Grid.singleDigits(input)

    assertThat(Vec2i(0, 0) in grid).isTrue()
    assertThat(Vec2i(-1, 0) in grid).isFalse()
    assertThat(Vec2i(0, -1) in grid).isFalse()
    assertThat(Vec2i(5, 0) in grid).isFalse()
    assertThat(Vec2i(0, 2) in grid).isFalse()
  }

  @Test fun testMapToXCoord() {
    val input = """
      123
      456
      789
    """.trimIndent()
    val grid = Grid.singleDigits(input).map { (x, y), _ -> x }

    assertThat(grid.toDigitString()).isEqualTo("""
      012
      012
      012
    """.trimIndent().trim())
  }

  @Test fun testMapToYCoord() {
    val input = """
      123
      456
      789
    """.trimIndent()
    val grid = Grid.singleDigits(input).map { (x, y), _ -> y }

    assertThat(grid.toDigitString()).isEqualTo("""
      000
      111
      222
    """.trimIndent().trim())
  }

  @Test fun testMapPlusOne() {
    val input = """
      012
      345
      678
    """.trimIndent()
    val grid = Grid.singleDigits(input).map { _, v -> v + 1 }

    assertThat(grid.toDigitString()).isEqualTo("""
      123
      456
      789
    """.trimIndent().trim())
  }

  @Test fun testStringBorderWidthOne() {
    val input = """
      012
      345
      678
    """.trimIndent()
    val grid = Grid.singleDigits(input).map { _, v -> v + 1 }

    assertThat(grid.borderWith(0).toDigitString()).isEqualTo("""
      00000
      01230
      04560
      07890
      00000
    """.trimIndent().trim())
  }

  @Test fun testStringBorderWidthTwo() {
    val input = """
      012
      345
      678
    """.trimIndent()
    val grid = Grid.singleDigits(input).map { _, v -> v + 1 }

    assertThat(grid.borderWith(0, borderWidth = 2).toDigitString()).isEqualTo("""
      0000000
      0000000
      0012300
      0045600
      0078900
      0000000
      0000000
    """.trimIndent().trim())
  }
}