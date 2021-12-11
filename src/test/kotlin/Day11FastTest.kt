import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.StringBuilder

class Day11FastTest {
  val grid =
    Day11Fast.parser(readFile("day11_test"))
      .cells.map { (_, v) -> v }.toIntArray()

  private fun IntArray.toDigitString(): String {
    val sb = StringBuilder()
    for (y in 0 until 10) {
      for (x in 0 until 10) {
        sb.append(this[y * 10 + x].toString())
      }
      sb.append("\n")
    }
    return sb.toString().trim()
  }

  @Test fun testEvolveDay1() {
    val flashes = Day11Fast.evolve(grid)

    assertThat(flashes).isEqualTo(0)
    assertThat(grid.toDigitString()).isEqualTo("""
      6594254334
      3856965822
      6375667284
      7252447257
      7468496589
      5278635756
      3287952832
      7993992245
      5957959665
      6394862637
    """.trimIndent().trim())
  }

  @Test fun testEvolveDay2() {
    val flashes = evolve(2)

    assertThat(flashes).isEqualTo(35)
    assertThat(grid.toDigitString()).isEqualTo("""
      8807476555
      5089087054
      8597889608
      8485769600
      8700908800
      6600088989
      6800005943
      0000007456
      9000000876
      8700006848
    """.trimIndent().trim())
  }

  @Test fun testEvolveDay10() {
    val flashes = evolve(10)

    assertThat(flashes).isEqualTo(204)
    assertThat(grid.toDigitString()).isEqualTo("""
      0481112976
      0031112009
      0041112504
      0081111406
      0099111306
      0093511233
      0442361130
      5532252350
      0532250600
      0032240000
    """.trimIndent().trim())
  }

  fun evolve(days: Int): Int {
    var flashes = 0
    repeat(days) {
      flashes += Day11Fast.evolve(grid)
    }
    return flashes
  }
}
