package utils

import kotlin.math.abs

fun Point3i(x: Int, y: Int, z: Int) = Vec4i(x, y, z, 1)
fun Vec3i(x: Int, y: Int, z: Int) = Vec4i(x, y, z, 0)

enum class Component4 { X, Y, Z, W }

data class Vec4i(val x: Int, val y: Int, val z: Int, val w: Int) {
  operator fun minus(o: Vec4i) = Vec4i(x - o.x, y - o.y, z - o.z, w - o.w)

  operator fun get(c: Component4): Int {
    return when (c) {
      Component4.X -> x
      Component4.Y -> y
      Component4.Z -> z
      Component4.W -> w
    }
  }

  fun copy(c: Component4, v: Int): Vec4i {
    return when (c) {
      Component4.X -> copy(x = v)
      Component4.Y -> copy(y = v)
      Component4.Z -> copy(z = v)
      Component4.W -> copy(w = v)
    }
  }

  override fun toString(): String {
    return "${if (w == 1) 'p' else 'v'}($x,$y,$z)"
  }

  fun distanceSqr(o: Vec4i): Int {
    val d = this - o
    return d.x * d.x + d.y * d.y + d.z * d.z
  }

  fun distanceManhattan(o: Vec4i): Int {
    val d = this - o
    return abs(d.x) + abs(d.y) + abs(d.z)
  }

  fun coerceAtLeast(value: Vec4i) = Vec4i(
    x.coerceAtLeast(value.x),
    y.coerceAtLeast(value.y),
    z.coerceAtLeast(value.z),
    w.coerceAtLeast(value.w)
  )

  fun coerceAtMost(value: Vec4i) = Vec4i(
    x.coerceAtMost(value.x),
    y.coerceAtMost(value.y),
    z.coerceAtMost(value.z),
    w.coerceAtMost(value.w)
  )

  val asPoint get() = copy(w = 1)
  val asVector get() = copy(w = 0)
}

data class Mat4i(
  val p00: Int, val p10: Int, val p20: Int, val p30: Int,
  val p01: Int, val p11: Int, val p21: Int, val p31: Int,
  val p02: Int, val p12: Int, val p22: Int, val p32: Int,
  val p03: Int, val p13: Int, val p23: Int, val p33: Int,
) {
  operator fun times(o: Mat4i): Mat4i {
    return Mat4i(
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

  operator fun times(o: Vec4i): Vec4i {
    return Vec4i(
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
    fun translate(v: Vec4i) = Mat4i(
      1, 0, 0, v.x,
      0, 1, 0, v.y,
      0, 0, 1, v.z,
      0, 0, 0, v.w
    )
  }
}

/**
 * Transformation matrix to rotate around X-axis by 90 degrees
 */
val ROT_X_90 = Mat4i(
  1, 0, 0, 0,
  0, 0, -1, 0,
  0, 1, 0, 0,
  0, 0, 0, 1
)

/**
 * Transformation matrix to rotate around Y-axis by 90 degrees
 */
val ROT_Y_90 = Mat4i(
  0, 0, 1, 0,
  0, 1, 0, 0,
  -1, 0, 0, 0,
  0, 0, 0, 1
)

/**
 * Transformation matrix to rotate around Z-axis by 90 degrees
 */
val ROT_Z_90 = Mat4i(
  0, -1, 0, 0,
  1, 0, 0, 0,
  0, 0, 1, 0,
  0, 0, 0, 1
)

/**
 * All possible rotations of a point in 3-dimensional space
 */
val ROTS = (0..3).flatMap { xs ->
  (0..3).flatMap { ys ->
    (0..3).map { zs ->
      List(xs) { ROT_X_90 } + List(ys) { ROT_Y_90 } + List(zs) { ROT_Z_90 }
    }
  }
}.filter { it.isNotEmpty() }.map { it.reduce { acc, m -> acc * m } }.toSet()
