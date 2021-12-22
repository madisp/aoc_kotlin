import Day22.Cuboid

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import utils.Component4.X
import utils.Component4.Y
import utils.Component4.Z
import utils.Point3i

class Day22Test {
  fun cuboid(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) =
    Cuboid(Point3i(x1, y1, z1), Point3i(x2, y2, z2))

  @Test fun testCutX() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(X, 5)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 5, 10, 10))
    assertThat(r).isEqualTo(cuboid(5, 0, 0, 10, 10, 10))
  }

  @Test fun testCutXStartSide() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(X, 0)
    assertThat(l).isNull()
    assertThat(r).isEqualTo(cuboid(0, 0, 0, 10, 10, 10))
  }

  @Test fun testCutXStartSide1() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(X, 1)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 1, 10, 10))
    assertThat(r).isEqualTo(cuboid(1, 0, 0, 10, 10, 10))
  }

  @Test fun testCutXEndSide() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(X, 10)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 10, 10, 10))
    assertThat(r).isNull()
  }

  @Test fun testCutXEndSide1() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(X, 9)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 9, 10, 10))
    assertThat(r).isEqualTo(cuboid(9, 0, 0, 10, 10, 10))
  }

  @Test fun testCutY() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(Y, 5)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 10, 5, 10))
    assertThat(r).isEqualTo(cuboid(0, 5, 0, 10, 10, 10))
  }

  @Test fun testCutZ() {
    val (l, r) = cuboid(0, 0, 0, 10, 10, 10).cut(Z, 5)
    assertThat(l).isEqualTo(cuboid(0, 0, 0, 10, 10, 5))
    assertThat(r).isEqualTo(cuboid(0, 0, 5, 10, 10, 10))
  }

  @Test fun testMinusEnd() {
    val cuboids = cuboid(0, 0, 0, 10, 10, 10) - cuboid(5, 5, 5, 15, 15, 15)
    assertThat(cuboids).containsExactly(
      // startx
      cuboid(0, 0, 0, 5, 10, 10),
      // cuboid(4, 0, 0, 10, 10, 10) -> split further

      // starty
      cuboid(5, 0, 0, 10, 5, 10),
      // cuboid(5, 5, 0, 10, 10, 10) -> split further

      // startz
      cuboid(5, 5, 0, 10, 10, 5),
      // cuboid(5, 5, 5, 10, 10, 10) -> intersection
    )
  }

  @Test fun testMinusStart() {
    val cuboids = cuboid(5, 5, 5, 15, 15, 15) - cuboid(0, 0, 0, 10, 10, 10)
    assertThat(cuboids).containsExactly(
      // endx
      cuboid(10, 5, 5, 15, 15, 15),
      // cuboid(5, 5, 5, 10, 15, 15) -> split further

      // endy
      cuboid(5, 10, 5, 10, 15, 15),
      // cuboid(5, 5, 5, 10, 10, 15) -> split further

      // endz
      cuboid(5, 5, 10, 10, 10, 15),
      // cuboid(5, 5, 5, 10, 10, 10) -> intersection
    )
  }

  @Test fun testMinusOutside() {
    val cuboids = cuboid(3, 3, 3, 5, 5, 5) - cuboid(0, 0, 0, 10, 10, 10)
    assertThat(cuboids).isEmpty()
  }

  @Test fun testIntersectInside() {
    val cuboid = cuboid(3, 3, 3, 5, 5, 5) intersect cuboid(0, 0, 0, 10, 10, 10)
    assertThat(cuboid).isEqualTo(cuboid(3, 3, 3, 5, 5, 5))
  }

  @Test fun testMinusInside() {
    val cuboids = cuboid(0, 0, 0, 10, 10, 10) - cuboid(3, 3, 3, 5, 5, 5)
    assertThat(cuboids).containsAtLeast(
      // startx
      cuboid(0, 0, 0, 3, 10, 10),
      // cuboid(3, 0, 0, 10, 10, 10) -> split further

      // endx
      cuboid(5, 0, 0, 10, 10, 10),
      // cuboid(3, 0, 0, 5, 10, 10) -> split further

      // starty
      cuboid(3, 0, 0, 5, 3, 10),
      // cuboid(3, 3, 0, 5, 10, 10) -> split further

      // endy
      cuboid(3, 5, 0, 5, 10, 10),
      // cuboid(3, 3, 0, 5, 5, 10) -> split further

      // startz
      cuboid(3, 3, 0, 5, 5, 3),
      // cuboid(3, 3, 3, 5, 5, 10) -> split further

      // endz
      cuboid(3, 3, 5, 5, 5, 10),
      // cuboid(3, 3, 3, 5, 5, 5) -> intersection
    )
  }

  @Test fun testMinusEdge() {
    val left = cuboid(0, 0, 0, 5, 5, 5)
    val right = cuboid(4, 0, 0, 8, 5, 5)
    assertThat(left - right).containsExactly(
      cuboid(0, 0, 0, 4, 5, 5)
    )

    assertThat(right - left).containsExactly(
      cuboid(5, 0, 0, 8, 5, 5)
    )

    assertThat(left intersect right).isEqualTo(cuboid(4, 0, 0, 5, 5, 5))
  }
}
