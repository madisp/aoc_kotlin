package utils

import kotlin.math.abs

@Suppress("FunctionName")
fun Point3l(x: Long, y: Long, z: Long) = Vec4l(x, y, z, 1)
@Suppress("FunctionName")
fun Vec3l(x: Long, y: Long, z: Long) = Vec4l(x, y, z, 0)

data class Vec4l(val x: Long, val y: Long, val z: Long, val w: Long) {
  operator fun minus(o: Vec4l) = Vec4l(x - o.x, y - o.y, z - o.z, w - o.w)

  operator fun plus(o: Vec4l) = Vec4l(x + o.x, y + o.y, z + o.z, w + o.w)

  operator fun times(v: Long) = Vec4l(x * v, y * v, z * v, w * v)

  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
    copy(z = z - 1),
    copy(z = z + 1)
  )

  operator fun get(c: Component4): Long {
    return when (c) {
      Component4.X -> x
      Component4.Y -> y
      Component4.Z -> z
      Component4.W -> w
    }
  }

  fun copy(c: Component4, v: Long): Vec4l {
    return when (c) {
      Component4.X -> copy(x = v)
      Component4.Y -> copy(y = v)
      Component4.Z -> copy(z = v)
      Component4.W -> copy(w = v)
    }
  }

  override fun toString(): String {
    return "($x,$y,$z,$w)"
  }

  fun distanceSqr(o: Vec4l): Long {
    val d = this - o
    return d.x * d.x + d.y * d.y + d.z * d.z
  }

  fun distanceManhattan(o: Vec4l): Long {
    val d = this - o
    return abs(d.x) + abs(d.y) + abs(d.z)
  }

  fun coerceAtLeast(value: Vec4l) = Vec4l(
    x.coerceAtLeast(value.x),
    y.coerceAtLeast(value.y),
    z.coerceAtLeast(value.z),
    w.coerceAtLeast(value.w)
  )

  fun coerceAtMost(value: Vec4l) = Vec4l(
    x.coerceAtMost(value.x),
    y.coerceAtMost(value.y),
    z.coerceAtMost(value.z),
    w.coerceAtMost(value.w)
  )

  val asPoint get() = copy(w = 1)
  val asVector get() = copy(w = 0)
}

data class Mat4l(
  val p00: Long, val p10: Long, val p20: Long, val p30: Long,
  val p01: Long, val p11: Long, val p21: Long, val p31: Long,
  val p02: Long, val p12: Long, val p22: Long, val p32: Long,
  val p03: Long, val p13: Long, val p23: Long, val p33: Long,
) {
  operator fun times(o: Mat4l): Mat4l {
    return Mat4l(
      p00 * o.p00 + p10 * o.p01 + p20 * o.p02 + p30 * o.p03,
      p00 * o.p10 + p10 * o.p11 + p20 * o.p12 + p30 * o.p13,
      p00 * o.p20 + p10 * o.p21 + p20 * o.p22 + p30 * o.p23,
      p00 * o.p30 + p10 * o.p31 + p20 * o.p32 + p30 * o.p33,

      p01 * o.p00 + p11 * o.p01 + p21 * o.p02 + p31 * o.p03,
      p01 * o.p10 + p11 * o.p11 + p21 * o.p12 + p31 * o.p13,
      p01 * o.p20 + p11 * o.p21 + p21 * o.p22 + p31 * o.p23,
      p01 * o.p30 + p11 * o.p31 + p21 * o.p32 + p31 * o.p33,

      p02 * o.p00 + p12 * o.p01 + p22 * o.p02 + p32 * o.p03,
      p02 * o.p10 + p12 * o.p11 + p22 * o.p12 + p32 * o.p13,
      p02 * o.p20 + p12 * o.p21 + p22 * o.p22 + p32 * o.p23,
      p02 * o.p30 + p12 * o.p31 + p22 * o.p32 + p32 * o.p33,

      p03 * o.p00 + p13 * o.p01 + p23 * o.p02 + p33 * o.p03,
      p03 * o.p10 + p13 * o.p11 + p23 * o.p12 + p33 * o.p13,
      p03 * o.p20 + p13 * o.p21 + p23 * o.p22 + p33 * o.p23,
      p03 * o.p30 + p13 * o.p31 + p23 * o.p32 + p33 * o.p33,
    )
  }

  operator fun times(o: Vec4l): Vec4l {
    return Vec4l(
      p00 * o.x + p10 * o.y + p20 * o.z + p30 * 1,
      p01 * o.x + p11 * o.y + p21 * o.z + p31 * 1,
      p02 * o.x + p12 * o.y + p22 * o.z + p32 * 1,
      p03 * o.x + p13 * o.y + p23 * o.z + p33 * 1
    )
  }

  companion object {
    /**
     * Create a translation matrix given a vector
     */
    fun translate(v: Vec4l) = Mat4l(
      1, 0, 0, v.x,
      0, 1, 0, v.y,
      0, 0, 1, v.z,
      0, 0, 0, v.w
    )
  }
}
