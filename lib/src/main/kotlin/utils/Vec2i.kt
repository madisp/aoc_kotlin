package utils

import kotlin.math.abs

fun Char.toVec2i() = when (this) {
  '^' -> Vec2i.UP
  '>' -> Vec2i.RIGHT
  'v' -> Vec2i.DOWN
  '<' -> Vec2i.LEFT
  else -> throw IllegalArgumentException("Only ^>v< can be converted to a vec")
}

data class Vec2i(val x: Int, val y: Int) {
  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1)
  )

  val surrounding get() = grow().filter { (x, y) -> x != this.x || y != this.y }

  fun grow(amount: Int = 1) = (x - amount .. x + amount).flatMap { nx ->
    (y - amount .. y + amount).map { ny ->
      Vec2i(nx, ny)
    }
  }

  fun rotateCw() = Vec2i(y, -x)

  fun rotateCcw() = Vec2i(-y, x)

  operator fun plus(other: Vec2i) = Vec2i(this.x + other.x, this.y + other.y)

  operator fun minus(other: Vec2i) = Vec2i(this.x - other.x, this.y - other.y)

  operator fun times(scalar: Int) = Vec2i(this.x * scalar, this.y * scalar)

  operator fun plus(scalar: Int) = Vec2i(this.x + scalar, this.y + scalar)

  operator fun div(scalar: Int) = Vec2i(this.x / scalar, this.y / scalar)

  operator fun rem(other: Vec2i) = Vec2i(this.x % other.x, this.y % other.y)

  fun manhattanDistanceTo(other: Vec2i): Int {
    return (abs(other.x - x) + abs(other.y - y))
  }

  fun toVec2l() = Vec2l(x.toLong(), y.toLong())

  val gridChar: Char get() = when (this) {
    UP -> '^'
    RIGHT -> '>'
    DOWN -> 'v'
    LEFT -> '<'
    else -> throw IllegalArgumentException("Only CARDINAL directions (UP, RIGHT, DOWN, LEFT) can be converted to grid chars")
  }

  companion object {
    fun parse(str: String, delimiter: String = ","): Vec2i {
      return str.cut(delimiter, String::toInt, String::toInt, ::Vec2i)
    }

    val UP = Vec2i(0, -1)
    val DOWN = Vec2i(0, 1)
    val LEFT = Vec2i(-1, 0)
    val RIGHT = Vec2i(1, 0)

    val CARDINALS = listOf(UP, DOWN, LEFT, RIGHT)
    val DIAGONALS = listOf(UP + LEFT, UP + RIGHT, DOWN + RIGHT, DOWN + LEFT)
  }
}

val Collection<Vec2i>.bounds: Pair<Vec2i, Vec2i> get() {
  return Vec2i(minOf { it.x }, minOf { it.y }) to Vec2i(maxOf { it.x }, maxOf { it.y })
}

data class Segment(val start: Vec2i, val end: Vec2i) {
  companion object {
    fun parse(str: String, delimiter: String = "->"): Segment {
      return str.cut(delimiter, Vec2i.Companion::parse, Vec2i.Companion::parse, ::Segment)
    }
  }

  val isVertical = start.x == end.x
  val isHorizontal = start.y == end.y

  val slope: Int get() = (end.y - start.y) / (end.x - start.x)

  val points: List<Vec2i> get() = when {
    isVertical -> (minOf(start.y, end.y) .. maxOf(start.y, end.y)).map { y -> Vec2i(start.x, y) }
    else -> (minOf(start.x, end.x) .. maxOf(start.x, end.x)).map { x -> get(x) }
  }

  operator fun get(x: Int): Vec2i {
    if (isVertical) {
      throw IllegalStateException("Can not get point by x for a vertical line")
    }

    val dx = (x - start.x)
    val y = start.y + dx * slope
    return Vec2i(x, y)
  }

  operator fun contains(p: Vec2i): Boolean {
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
