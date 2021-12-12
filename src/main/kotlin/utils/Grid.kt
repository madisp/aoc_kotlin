package utils

open class Grid(
  private val arr: IntArray,
  val width: Int,
  val height: Int
) {
  open class Column(private val grid: Grid, private val x: Int) {
    operator fun get(y: Int) = grid.get(x, y)
  }

  init {
    if (arr.size != width * height) {
      throw IllegalStateException("Input arr size=${arr.size} != $width x $height")
    }
  }

  val coords: Collection<Coord> get() = (0 until height).flatMap { y ->
    (0 until width).map { x ->
      Coord(x, y)
    }
  }

  val cells: Collection<Pair<Coord, Int>> get() = coords.map { it to this[it] }
  val values: Collection<Int> get() = arr.asList()

  operator fun contains(c: Coord) = c.x in (0 until width) && c.y in (0 until height)

  fun get(x: Int, y: Int): Int = arr[y * width + x]

  open operator fun get(x: Int) = Column(this, x)

  operator fun get(c: Coord): Int = get(c.x, c.y)

  fun map(fn: (Coord, Int) -> Int) = Grid(
    IntArray(arr.size) { i -> fn(Coord(i % width, i / height), arr[i]) },
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
    val singleDigits = Parser { input ->
      val stride = input
        .splitToSequence("\n")
        .filter(String::isNotBlank)
        .map { it.trim().length }
        .first()

      fromDigits(input, stride)
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
