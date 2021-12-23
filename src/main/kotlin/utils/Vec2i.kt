package utils

data class Vec2i(val x: Int, val y: Int) {
  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1)
  )

  val surrounding get() = grow().filter { (x, y) -> x != this.x || y != this.y }

  fun grow() = (x - 1 .. x + 1).flatMap { nx ->
    (y - 1 .. y + 1).map { ny ->
      Vec2i(nx, ny)
    }
  }


  companion object {
    fun parse(str: String, delimiter: String = ","): Vec2i {
      return str.cut(delimiter, String::toInt, String::toInt, ::Vec2i)
    }
  }
}

data class Segment(val start: Vec2i, val end: Vec2i) {
  companion object {
    fun parse(str: String, delimiter: String = "->"): Segment {
      return str.cut(delimiter, Vec2i::parse, Vec2i::parse, ::Segment)
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
