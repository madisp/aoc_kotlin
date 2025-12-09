package utils

import kotlin.math.abs

data class Vec2d(val x: Double, val y: Double) {
  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1)
  )

  fun rotateCw() = Vec2d(y, -x)

  fun rotateCcw() = Vec2d(-y, x)

  operator fun plus(other: Vec2d) = Vec2d(this.x + other.x, this.y + other.y)

  operator fun minus(other: Vec2d) = Vec2d(this.x - other.x, this.y - other.y)

  operator fun times(scalar: Double) = Vec2d(this.x * scalar, this.y * scalar)

  operator fun plus(scalar: Double) = Vec2d(this.x + scalar, this.y + scalar)

  operator fun div(scalar: Double) = Vec2d(this.x / scalar, this.y / scalar)

  operator fun rem(other: Vec2d) = Vec2d(this.x % other.x, this.y % other.y)

  fun manhattanDistanceTo(other: Vec2d): Double {
    return (abs(other.x - x) + abs(other.y - y))
  }

  operator fun compareTo(o: Vec2d): Int {
    return when {
      x != o.x -> x.compareTo(o.x)
      else -> y.compareTo(o.y)
    }
  }

  companion object {
    fun parse(str: String, delimiter: String = ","): Vec2d {
      return str.cut(delimiter, String::toDouble, String::toDouble, ::Vec2d)
    }

    val UP = Vec2d(0.0, -1.0)
    val DOWN = Vec2d(0.0, 1.0)
    val LEFT = Vec2d(-1.0, 0.0)
    val RIGHT = Vec2d(1.0, 0.0)
  }
}

val Collection<Vec2d>.bounds: Pair<Vec2d, Vec2d> get() {
  return Vec2d(minOf { it.x }, minOf { it.y }) to Vec2d(maxOf { it.x }, maxOf { it.y })
}

data class SegmentD(val start: Vec2d, val end: Vec2d) {
  val isVertical = start.x == end.x
  val isHorizontal = start.y == end.y

  val slope: Double get() = (end.y - start.y) / (end.x - start.x)

  operator fun get(x: Double): Vec2d {
    if (isVertical) {
      throw IllegalStateException("Can not get point by x for a vertical line")
    }

    val dx = (x - start.x)
    val y = start.y + dx * slope
    return Vec2d(x, y)
  }

  operator fun contains(p: Vec2d): Boolean {
    if (p.y !in (minOf(start.y, end.y) .. maxOf(start.y, end.y))) {
      return false
    }
    if (p.x !in (start.x .. end.x)) {
      return false
    }

    // vertical line
    if (end.x == start.x) {
      return true // p.x is always start.x due to the bounds check above
    } else {
      return this[p.x] == p
    }
  }
}
