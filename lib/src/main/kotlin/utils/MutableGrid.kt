package utils

inline fun <reified T> createMutableGrid(width: Int, height: Int, valuesFn: (Vec2i) -> T): MutableGrid<T> {
  val arr = Array(width * height) { index -> valuesFn(Vec2i(index % width, index / width)) }
  return MutableGrid(arr, width, height)
}

class MutableGrid<T>(private val arr: Array<T>, width: Int, height: Int) : Grid<T>(arr, width, height) {
  class MutableColumn<T>(private val grid: MutableGrid<T>, x: Int) : Column<T>(grid, x) {
    operator fun set(y: Int, value: T) = grid.set(x, y, value)
  }

  class MutableRow<T>(private val grid: MutableGrid<T>, y: Int): Row<T>(grid, y) {
    operator fun set(x: Int, value: T) = grid.set(x, y, value)
  }

  override val columns: List<MutableColumn<T>> get() = (0 until width).map { this[it] }
  override val rows: List<MutableRow<T>> get() = (0 until height).map { this.getRow(it) }

  override operator fun get(x: Int) = MutableColumn(this, x)
  override fun getRow(y: Int) = MutableRow(this, y)

  fun set(x: Int, y: Int, value: T) {
    arr[y * width + x] = value
  }

  operator fun set(c: Vec2i, value: T) {
    set(c.x, c.y, value)
  }

  fun swap(c1: Vec2i, c2: Vec2i) {
    val value = this[c1]
    this[c1] = this[c2]
    this[c2] = value
  }

  fun copyTo(target: MutableGrid<T>) {
    System.arraycopy(arr, 0, target.arr, 0, arr.size)
  }

  fun clone(): MutableGrid<T> = MutableGrid(arr.clone(), width, height)
}
