package utils

data class Coord(val x: Int, val y: Int) {
  val adjacent get() = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1)
  )

  val surrounding get() = (x - 1 .. x + 1).flatMap { nx ->
    (y - 1 .. y + 1).map { ny ->
      Coord(nx, ny)
    }
  }.filter { (x, y) -> x != this.x || y != this.y }
}
