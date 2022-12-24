package utils

inline fun <reified T> createGrid(width: Int, height: Int, valuesFn: (Vec2i) -> T): Grid<T> {
  val arr = Array(width * height) { index -> valuesFn(Vec2i(index % width, index / width)) }
  return Grid(arr, width, height)
}

/**
 * Generic equivalent of [IntGrid], the latter is Int-specialized for performance
 */
open class Grid<T>(
  private val arr: Array<T>,
  val width: Int,
  val height: Int
) {

  open class Column<T>(private val grid: Grid<T>, private val x: Int) {
    operator fun get(y: Int) = grid.get(x, y)
    val cells: Collection<Pair<Vec2i, T>> get() = (0 until grid.height).map { y -> Vec2i(x, y) to grid[x][y] }
    val values: Collection<T> get() = (0 until grid.height).map { y -> grid[x][y] }
  }

  open class Row<T>(private val grid: Grid<T>, private val y: Int) {
    operator fun get(x: Int) = grid.get(x, y)
    val cells: Collection<Pair<Vec2i, T>> get() = (0 until grid.width).map { x -> Vec2i(x, y) to grid[x][y] }
    val values: Collection<T> get() = (0 until grid.width).map { x -> grid[x][y] }
  }

  init {
    if (arr.size != width * height) {
      throw IllegalStateException("Input arr size=${arr.size} != $width x $height")
    }
  }

  val coords: Collection<Vec2i> get() = (0 until height).flatMap { y ->
    (0 until width).map { x ->
      Vec2i(x, y)
    }
  }

  fun bounds(test: (T) -> Boolean): Pair<Vec2i, Vec2i> {
    return cells.filter { (_, v) -> test(v) }.map { it.first }.bounds
  }

  val cells: Collection<Pair<Vec2i, T>> get() = coords.map { it to this[it] }
  val values: Collection<T> get() = arr.asList()

  val columns: Collection<Column<T>> get() = (0 until width).map { this[it] }
  val rows: Collection<Row<T>> get() = (0 until height).map { getRow(it) }

  operator fun contains(c: Vec2i) = c.x in (0 until width) && c.y in (0 until height)

  fun get(x: Int, y: Int): T = arr[y * width + x]
  operator fun get(c: Vec2i): T = get(c.x, c.y)

  // get col, row
  open operator fun get(x: Int) = Column(this, x)
  open fun getRow(y: Int) = Row(this, y)

  override fun equals(other: Any?): Boolean {
    if (other is Grid<*>) {
      if (width == other.width && height == other.height) {
        return values == other.values
      }
    }
    return false
  }

  companion object {
    inline fun <reified T> empty() = Grid<T>(emptyArray(), 0, 0)

    fun chars(padChar: Char = ' ') = Parser { input ->
      val lines = input.split("\n")
      val height = lines.size
      require(height > 0)

      val width = lines.maxOf { it.length }

      createGrid(width, height) { (x, y) -> lines.getOrNull(y)?.getOrNull(x) ?: padChar }
    }
  }
}

val Grid<Char>.debugString: String get() {
  return rows.map { it.values.joinToString("") }.joinToString("\n")
}

inline fun <reified T> Grid<T>.borderWith(value: T, borderWidth: Int = 1): Grid<T> {
  return createGrid<T>(width + borderWidth * 2, height + borderWidth * 2) { (x, y) ->
    if (x < borderWidth || y < borderWidth || x >= width + borderWidth || y >= height + borderWidth) {
      value
    } else {
      this[x - borderWidth][y - borderWidth]
    }
  }
}

inline fun <T, reified R> Grid<T>.map(fn: (Vec2i, T) -> R) = createGrid(width, height) { pos -> fn(pos, this[pos.x][pos.y]) }
