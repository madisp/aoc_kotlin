import com.google.common.truth.Truth.assertThat
import org.junit.Test
import utils.readFile

class Day11ImpTest {
  val grid = Day11Imp.parser(readFile("day11_test")).toMutable()

  @Test fun testParse() {
    assertThat(grid.toDigitString()).isEqualTo("""
      5483143223
      2745854711
      5264556173
      6141336146
      6357385478
      4167524645
      2176841721
      6882881134
      4846848554
      5283751526
    """.trimIndent().trim())
  }

  @Test fun testEvolveDay1() {
    val flashes = Day11Imp.evolve(grid)

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
      flashes += Day11Imp.evolve(grid)
    }
    return flashes
  }
}
