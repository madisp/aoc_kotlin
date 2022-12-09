package utils

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

  operator fun plus(other: Vec2i) = Vec2i(this.x + other.x, this.y + other.y)

  companion object {
    fun parse(str: String, delimiter: String = ","): Vec2i {
      return str.cut(delimiter, String::toInt, String::toInt, ::Vec2i)
    }
  }
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
