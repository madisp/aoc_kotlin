import utils.Grid
import utils.IntGrid
import utils.Solution
import utils.Vec2i

fun main() {
  Day3_fast.run(skipTest = false)
}

object Day3All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day3, "fast" to Day3_fast).forEach { (header, solution) ->
      solution.run(
        header = header,
        printParseTime = false,
        skipTest = false,
        skipPart1 = false,
      )
    }
  }
}

object Day3_fast : Solution<Grid<Char>>() {
  override val name = "day3"
  override val parser = Grid.chars(oobBehaviour = Grid.OobBehaviour.Default('.'))

  override fun part1(input: Grid<Char>): Int {
    var sum = 0

    input.rows.forEach { row ->
      var curNum = 0
      var start: Vec2i? = null
      var isAdjacent = false

      row.cells.forEach { (p, c) ->
        if (c.isDigit()) {
          if (start == null) {
            // left here
            if (input[p.x - 1][p.y].isSymbol()) {
              isAdjacent = true
            } else if (p.y - 1 >= 0 && input[p.x - 1][p.y - 1].isSymbol()) {
              isAdjacent = true
            } else if (p.y + 1 < input.height - 1 && input[p.x - 1][p.y + 1].isSymbol()) {
              isAdjacent = true
            }
            start = p
          }
          curNum = curNum * 10 + (c - '0')
          // up and down here
          if (!isAdjacent) {
            if (input[p.x][p.y - 1].isSymbol()) {
              isAdjacent = true
            } else if (input[p.x][p.y + 1].isSymbol()) {
              isAdjacent = true
            }
          }
        } else {
          // right here
          if (curNum != 0) {
            if (!isAdjacent) {
              if (input[p.x][p.y].isSymbol()) {
                isAdjacent = true
              } else if (input[p.x][p.y - 1].isSymbol()) {
                isAdjacent = true
              } else if (input[p.x][p.y + 1].isSymbol()) {
                isAdjacent = true
              }
            }

            if (isAdjacent) {
              sum += curNum
            }
          }
          curNum = 0
          start = null
          isAdjacent = false
        }
      }

      if (curNum != 0 && isAdjacent) {
        sum += curNum
      }
    }

    return sum
  }

  override fun part2(input: Grid<Char>): Int {
    var sum = 0
    val nums = IntGrid(input.width, input.height, 0).toMutable()
    val gears = mutableListOf<Vec2i>()

    input.rows.forEach { row ->
      var curNum = 0
      var start = 0

      row.cells.forEach { (p, c) ->
        if (c.isDigit()) {
          if (curNum == 0) {
            start = p.x
          }
          curNum = curNum * 10 + (c - '0')
        } else {
          if (c == '*') {
            gears.add(p)
          }

          // right here
          if (curNum != 0) {
            (start until p.x).forEach { x ->
              nums[x][p.y] = curNum
            }
            curNum = 0
          }
        }
      }

      if (curNum != 0) {
        (start until input.width).forEach { x ->
          nums[x][row.y] = curNum
        }
        curNum = 0
      }
    }

    gears.forEach { p ->
      var gear = 1
      var count = 0

      // left
      if (p.x - 1 >= 0 && nums[p.x - 1][p.y] != 0) {
        gear *= nums[p.x - 1][p.y]
        count++
      }

      // right
      if (p.x + 1 < input.width - 1 && nums[p.x + 1][p.y] != 0) {
        gear *= nums[p.x + 1][p.y]
        count++
      }

      // top
      if (p.y - 1 >= 0) {
        if (nums[p.x][p.y - 1] != 0) {
          gear *= nums[p.x][p.y - 1]
          count++
        } else {
          // top-left
          if (p.x - 1 >= 0 && nums[p.x - 1][p.y - 1] != 0) {
            gear *= nums[p.x - 1][p.y - 1]
            count++
          }
          // top-right
          if (p.x + 1 < input.width && nums[p.x + 1][p.y - 1] != 0) {
            gear *= nums[p.x + 1][p.y - 1]
            count++
          }
        }
      }

      // bottom
      if (p.y + 1 < input.height - 1) {
        if (nums[p.x][p.y + 1] != 0) {
          gear *= nums[p.x][p.y + 1]
          count++
        } else {
          // bottom-left
          if (p.x - 1 >= 0 && nums[p.x - 1][p.y + 1] != 0) {
            gear *= nums[p.x - 1][p.y + 1]
            count++
          }
          // bottom-right
          if (p.x + 1 < input.width && nums[p.x + 1][p.y + 1] != 0) {
            gear *= nums[p.x + 1][p.y + 1]
            count++
          }
        }
      }

      if (count == 2) {
        sum += gear
      }
    }

    return sum
  }
}

private fun Char.isSymbol(): Boolean {
  return !isDigit() && this != '.'
}
