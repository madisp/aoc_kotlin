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
