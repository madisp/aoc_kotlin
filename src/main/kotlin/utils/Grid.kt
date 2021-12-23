package utils

open class Grid(
  private val arr: IntArray,
  val width: Int,
  val height: Int
) {
  constructor(width: Int, height: Int, valuesFn: (Vec2i) -> Int) :
      this(IntArray(width * height) { index -> valuesFn(
        Vec2i(index % width, index / width)
      ) }, width, height)

  open class Column(private val grid: Grid, private val x: Int) {
    operator fun get(y: Int) = grid.get(x, y)
    val cells: Collection<Pair<Vec2i, Int>> get() = (0 until grid.height).map { y -> Vec2i(x, y) to grid[x][y] }
    val values: Collection<Int> get() = (0 until grid.height).map { y -> grid[x][y] }
  }

  open class Row(private val grid: Grid, private val y: Int) {
    operator fun get(x: Int) = grid.get(x, y)
    val cells: Collection<Pair<Vec2i, Int>> get() = (0 until grid.width).map { x -> Vec2i(x, y) to grid[x][y] }
    val values: Collection<Int> get() = (0 until grid.width).map { x -> grid[x][y] }
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

  val cells: Collection<Pair<Vec2i, Int>> get() = coords.map { it to this[it] }
  val values: Collection<Int> get() = arr.asList()

  val columns: Collection<Column> get() = (0 until width).map { this[it] }
  val rows: Collection<Row> get() = (0 until height).map { getRow(it) }

  operator fun contains(c: Vec2i) = c.x in (0 until width) && c.y in (0 until height)

  fun get(x: Int, y: Int): Int = arr[y * width + x]
  operator fun get(c: Vec2i): Int = get(c.x, c.y)

  // get col, row
  open operator fun get(x: Int) = Column(this, x)
  open fun getRow(y: Int) = Row(this, y)

  fun borderWith(value: Int, borderWidth: Int = 1): Grid {
    return Grid(width + borderWidth * 2, height + borderWidth * 2) { (x, y) ->
      if (x < borderWidth || y < borderWidth || x >= width + borderWidth || y >= height + borderWidth) {
        value
      } else {
        this[x - borderWidth][y - borderWidth]
      }
    }
  }

  fun map(fn: (Vec2i, Int) -> Int) = Grid(
    IntArray(arr.size) { i -> fn(Vec2i(i % width, i / height), arr[i]) },
    width,
    height
  )

  fun toMutable() = MutableGrid(arr.clone(), width, height)

  fun toDigitString(): String {
    return (0 until height).map { y ->
      (0 until width).map { x ->
        get(x, y).also {
          if (it > 10) {
            throw IllegalStateException("cannot serialize to digit string! Cell at x=$x y=$y is $it")
          }
        }
      }.joinToString("")
    }.joinToString("\n")
  }

  companion object {
    val EMPTY = Grid(intArrayOf(), 0, 0)

    val singleDigits = Parser { input ->
      val stride = input
        .splitToSequence("\n")
        .filter(String::isNotBlank)
        .map { it.trim().length }
        .first()

      fromDigits(input, stride)
    }

    val table = Parser { input ->
      val delimiter = " "
      val lines = input.split("\n").filter(String::isNotBlank)
      val stride = lines.first().split(delimiter).filter(String::isNotBlank).size
      fromLines(lines, stride, delimiter)
    }

    private fun fromLines(input: List<String>, stride: Int, delimiter: String = " "): Grid {
      val nums = input.filter(String::isNotBlank).flatMap {
        it.split(delimiter).filter(String::isNotBlank).map(String::toInt)
      }

      return Grid(nums.toIntArray(), stride, nums.size / stride)
    }

    private fun fromDigits(input: String, stride: Int): Grid {
      val nums = input.split("\n")
        .filter(String::isNotBlank)
        .map(String::toCharArray)
        .flatMap { chars -> chars.map { (it - '0') } }

      return Grid(nums.toIntArray(), stride, nums.size / stride)
    }
  }
}
