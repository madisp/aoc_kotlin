package utils

import kotlin.math.abs

data class Vec2l(val x: Long, val y: Long) {
  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1)
  )

  val surrounding get() = grow().filter { (x, y) -> x != this.x || y != this.y }

  fun grow(amount: Long = 1) = (x - amount .. x + amount).flatMap { nx ->
    (y - amount .. y + amount).map { ny ->
      Vec2l(nx, ny)
    }
  }

  fun rotateCw() = Vec2l(y, -x)

  fun rotateCcw() = Vec2l(-y, x)

  operator fun plus(other: Vec2l) = Vec2l(this.x + other.x, this.y + other.y)

  operator fun minus(other: Vec2l) = Vec2l(this.x - other.x, this.y - other.y)

  operator fun times(scalar: Long) = Vec2l(this.x * scalar, this.y * scalar)

  operator fun plus(scalar: Long) = Vec2l(this.x + scalar, this.y + scalar)

  operator fun div(scalar: Long) = Vec2l(this.x / scalar, this.y / scalar)

  operator fun rem(other: Vec2l) = Vec2l(this.x % other.x, this.y % other.y)

  fun manhattanDistanceTo(other: Vec2l): Long {
    return (abs(other.x - x) + abs(other.y - y))
  }

  companion object {
    fun parse(str: String, delimiter: String = ","): Vec2l {
      return str.cut(delimiter, String::toLong, String::toLong, ::Vec2l)
    }

    val UP = Vec2l(0, -1)
    val DOWN = Vec2l(0, 1)
    val LEFT = Vec2l(-1, 0)
    val RIGHT = Vec2l(1, 0)
  }
}

val Collection<Vec2l>.bounds: Pair<Vec2l, Vec2l> get() {
  return Vec2l(minOf { it.x }, minOf { it.y }) to Vec2l(maxOf { it.x }, maxOf { it.y })
}

data class SegmentL(val start: Vec2l, val end: Vec2l) {
  val isVertical = start.x == end.x
  val isHorizontal = start.y == end.y

  val slope: Long get() = (end.y - start.y) / (end.x - start.x)

  val points: List<Vec2l> get() = when {
    isVertical -> (minOf(start.y, end.y) .. maxOf(start.y, end.y)).map { y -> Vec2l(start.x, y) }
    else -> (minOf(start.x, end.x) .. maxOf(start.x, end.x)).map { x -> get(x) }
  }

  operator fun get(x: Long): Vec2l {
    if (isVertical) {
      throw IllegalStateException("Can not get point by x for a vertical line")
    }

    val dx = (x - start.x)
    val y = start.y + dx * slope
    return Vec2l(x, y)
  }

  operator fun contains(p: Vec2l): Boolean {
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
