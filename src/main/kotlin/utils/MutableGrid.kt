package utils

class MutableGrid(private val arr: IntArray, width: Int, height: Int) : Grid(arr, width, height) {
  class MutableColumn(private val grid: MutableGrid, private val x: Int) : Column(grid, x) {
    operator fun set(y: Int, value: Int) = grid.set(x, y, value)
  }

  class MutableRow(private val grid: MutableGrid, private val y: Int) : Row(grid, y) {
    operator fun set(x: Int, value: Int) = grid.set(x, y, value)
  }

  override operator fun get(x: Int) = MutableColumn(this, x)
  override fun getRow(y: Int) = MutableRow(this, y)

  fun set(x: Int, y: Int, value: Int) {
    arr[y * width + x] = value
  }

  operator fun set(c: Coord, value: Int) {
    set(c.x, c.y, value)
  }
}