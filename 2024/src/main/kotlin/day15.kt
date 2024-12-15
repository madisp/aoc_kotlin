import utils.*

fun main() {
  Day15.run()
}

typealias Day15In = Pair<Grid<Char>, String>

object Day15 : Solution<Day15In>() {
  override val name = "day15"
  override val parser: Parser<Day15In> = Parser.compound(
    Parser.charGrid,
    Parser { it.trim().replace("\n", "") }
  )

  override fun part1(input: Day15In): Any? {
    val g = input.first.toMutable()
    var robot = g.coords.first { g[it] == '@' }

    for (c in input.second) {
      val dir = c.toVec2i()
      var box = robot + dir
      while (g[box] == 'O') {
        box += dir
      }
      if (g[box] == '.') {
        // move
        var push = box - dir
        while (push != robot) {
          g[push + dir] = g[push]
          g[push] = '.'
          push -= dir
        }
        g[robot + dir] = '@'
        g[robot] = '.'
        robot += dir
      }
    }

    return g.cells
      .filter { (_, c) -> c == 'O' }
      .sumOf { (p, _) -> p.y * 100 + p.x }
  }

  private fun canPush(g: Grid<Char>, box: Vec2i, dir: Vec2i): Boolean {
    val side = if (g[box] == '[') {
      box.copy(x = box.x + 1)
    } else if (g[box] == ']') {
      box.copy(x = box.x - 1)
    } else badInput()

    return (g[box + dir] == '.' || (g[box + dir] in "[]" && canPush(g, box + dir, dir))) &&
        (g[side + dir] == '.' || (g[side + dir] in "[]" && canPush(g, side + dir, dir)))
  }

  private fun push(g: MutableGrid<Char>, box: Vec2i, dir: Vec2i) {
    val side = if (g[box] == '[') {
      box.copy(x = box.x + 1)
    } else if (g[box] == ']') {
      box.copy(x = box.x - 1)
    } else badInput()

    if (g[box + dir] in "[]") push(g, box + dir, dir)
    if (g[side + dir] in "[]") push(g, side + dir, dir)

    g[box + dir] = g[box]
    g[side + dir] = g[side]
    g[box] = '.'
    g[side] = '.'
  }

  override fun part2(input: Day15In): Any? {
    val g = createMutableGrid<Char>(input.first.width * 2, input.first.height) { '.' }

    input.first.cells.forEach { (p, c) ->
      val l = Vec2i(p.x * 2, p.y)
      val r = Vec2i(p.x * 2 + 1, p.y)
      when (c) {
        '@' -> { g[l] = '@'; g[r] = '.' }
        'O' -> { g[l] = '['; g[r] = ']' }
        else -> { g[l] = c; g[r] = c }
      }
    }

    var robot = g.coords.first { g[it] == '@' }

    for (c in input.second) {
      val dir = c.toVec2i()
      var box = robot + dir
      if (dir == Vec2i.LEFT || dir == Vec2i.RIGHT) {
        while (g[box] in "[]") {
          box += dir
        }
        if (g[box] == '.') {
          // move
          var push = box - dir
          while (push != robot) {
            g[push + dir] = g[push]
            g[push] = '.'
            push -= dir
          }
          g[robot + dir] = '@'
          g[robot] = '.'
          robot += dir
        }
      } else {
        if (g[box] in "[]" && canPush(g, box, dir)) {
          push(g, box, dir)
          g[robot + dir] = '@'
          g[robot] = '.'
          robot += dir
        } else if (g[box] == '.') {
          g[robot + dir] = '@'
          g[robot] = '.'
          robot += dir
        }
      }
    }

    return g.cells
      .filter { (_, c) -> c == '[' }
      .sumOf { (p, _) -> p.y * 100 + p.x }
  }
}
