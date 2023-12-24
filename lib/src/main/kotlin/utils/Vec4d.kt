package utils

import kotlin.math.abs

@Suppress("FunctionName")
fun Point3d(x: Double, y: Double, z: Double) = Vec4d(x, y, z, 1.0)
@Suppress("FunctionName")
fun Vec3d(x: Double, y: Double, z: Double) = Vec4d(x, y, z, 0.0)

data class Vec4d(val x: Double, val y: Double, val z: Double, val w: Double) {
  operator fun minus(o: Vec4d) = Vec4d(x - o.x, y - o.y, z - o.z, w - o.w)

  operator fun plus(o: Vec4d) = Vec4d(x + o.x, y + o.y, z + o.z, w + o.w)

  operator fun times(v: Double) = Vec4d(x * v, y * v, z * v, w * v)

  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
    copy(z = z - 1),
    copy(z = z + 1)
  )

  operator fun get(c: Component4): Double {
    return when (c) {
      Component4.X -> x
      Component4.Y -> y
      Component4.Z -> z
      Component4.W -> w
    }
  }

  fun copy(c: Component4, v: Double): Vec4d {
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

  fun distanceSqr(o: Vec4d): Double {
    val d = this - o
    return d.x * d.x + d.y * d.y + d.z * d.z
  }

  fun distanceManhattan(o: Vec4d): Double {
    val d = this - o
    return abs(d.x) + abs(d.y) + abs(d.z)
  }

  fun coerceAtLeast(value: Vec4d) = Vec4d(
    x.coerceAtLeast(value.x),
    y.coerceAtLeast(value.y),
    z.coerceAtLeast(value.z),
    w.coerceAtLeast(value.w)
  )

  fun coerceAtMost(value: Vec4d) = Vec4d(
    x.coerceAtMost(value.x),
    y.coerceAtMost(value.y),
    z.coerceAtMost(value.z),
    w.coerceAtMost(value.w)
  )

  val asPoint get() = copy(w = 1.0)
  val asVector get() = copy(w = 0.0)
}

val Collection<Vec4d>.bounds: Pair<Vec4d, Vec4d> get() {
  return Vec4d(minOf { it.x }, minOf { it.y }, minOf { it.z }, minOf { it.w }) to
    Vec4d(maxOf { it.x }, maxOf { it.y }, maxOf { it.z }, maxOf { it.w })
}
