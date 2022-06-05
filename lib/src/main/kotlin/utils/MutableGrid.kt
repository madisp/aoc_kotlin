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

  operator fun set(c: Vec2i, value: Int) {
    set(c.x, c.y, value)
  }

  fun swap(c1: Vec2i, c2: Vec2i) {
    val value = this[c1]
    this[c1] = this[c2]
    this[c2] = value
  }

  fun copyTo(target: MutableGrid) {
    System.arraycopy(arr, 0, target.arr, 0, arr.size)
  }

  fun clone(): MutableGrid = MutableGrid(arr.clone(), width, height)
}