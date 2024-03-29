package utils

class MutableIntGrid(private val arr: IntArray, width: Int, height: Int) : IntGrid(arr, width, height) {
  class MutableColumn(private val grid: MutableIntGrid, private val x: Int) : Column(grid, x) {
    operator fun set(y: Int, value: Int) = grid.set(x, y, value)
  }

  class MutableRow(private val grid: MutableIntGrid, private val y: Int) : Row(grid, y) {
    operator fun set(x: Int, value: Int) = grid.set(x, y, value)
  }

  override val columns: List<MutableColumn> get() = (0 until width).map { this[it] }
  override val rows: List<Row> get() = (0 until height).map { this.getRow(it) }

  override fun map(fn: (Vec2i, Int) -> Int): MutableIntGrid = super.map(fn).toMutable()

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

  fun copyTo(target: MutableIntGrid) {
    System.arraycopy(arr, 0, target.arr, 0, arr.size)
  }

  fun clone(): MutableIntGrid = MutableIntGrid(arr.clone(), width, height)
}
