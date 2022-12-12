import Day11Func.evolve
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import utils.IntGrid

class Day11FuncTest {
  val input = IntGrid.singleDigits("""
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
  """.trimIndent())

  @Test fun testEvolve1Day() {
    val step1 = evolve(input).drop(1).first().toDigitString()

    assertThat(step1).isEqualTo("""
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

  @Test fun testEvolve2Day() {
    val step2 = evolve(input).drop(2).first().toDigitString()

    assertThat(step2).isEqualTo("""
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
}
