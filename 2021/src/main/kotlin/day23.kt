import Day23.Cell.EMPTY
import utils.Parser
import utils.Point3i
import utils.Solution

fun main() {
  Day23.run()
}

object Day23 : Solution<Day23.Burrow>() {
  override val name = "day23"
  override val parser = Parser.lines.map { lines ->

    val roomCells = lines.drop(2).filter { "#########" !in it }
      .map { it.substring(3, 10).split('#').map { Cell.valueOf(it) } }

    val rooms = (0..3).map { idx -> Room(roomCells.map { it[idx] }) }

    Burrow(List(size = 7) { EMPTY }, rooms)
  }

  data class Burrow(
    val hallway: List<Cell>, // always size=7
    val rooms: List<Room>, // always size=4
  ) {

    val solved: Boolean get() {
      for (room in rooms.indices) {
        for (cell in rooms[room].cells) {
          if (cell.room != room) {
            return false
          }
        }
      }
      return true
    }

    fun swap(hallwayIndex: Int, roomIndex: Int): Burrow {
      val hallwayCell = hallway[hallwayIndex]

      val cellIndex = if (hallwayCell == EMPTY) {
        rooms[roomIndex].cells.indexOfFirst { it != EMPTY }
      } else {
        rooms[roomIndex].cells.indexOfLast { it == EMPTY }
      }

      val roomCell = rooms[roomIndex].cells[cellIndex]

      require(hallway[hallwayIndex] == EMPTY || rooms[roomIndex].cells[cellIndex] == EMPTY) {
        "Can't swap if one of the places isn't empty"
      }

      return copy(
        hallway = List(size = 7) { if (it == hallwayIndex) roomCell else hallway[it] },
        rooms = List(size = 4) {
          if (it == roomIndex) {
            rooms[it].copy(cells = rooms[it].cells.mapIndexed { index, cell -> if (index == cellIndex) hallway[hallwayIndex] else cell })
          } else rooms[it]
        }
      )
    }

    override fun toString(): String {
      return buildString {
        append("#############\n")
        append("#${hallway[0]}${hallway[1]}.${hallway[2]}.${hallway[3]}.${hallway[4]}.${hallway[5]}${hallway[6]}#\n")

        val cells = rooms.first().cells.size

        for (i in 0 until cells) {
          if (i == 0) {
            append("###")
          } else {
            append("  #")
          }

          for (room in rooms) {
            append("${room.cells[i]}#")
          }

          if (i == 0) {
            append("##\n")
          } else {
            append("  \n")
          }
        }

        append("  #########  ")
      }
    }
  }

  data class Room(
    val cells: List<Cell> // size = arbitrary
  )

  enum class Cell(val energy: Int, val room: Int) {
    EMPTY(0, -1),
    A(1, 0),
    B(10, 1),
    C(100, 2),
    D(1000, 3);

    override fun toString() = when (this) {
      EMPTY -> "."
      else -> this.name
    }
  }

  fun canMove(burrow: Burrow, from: Int, roomIndex: Int): Boolean {
    val left = roomIndex + 1
    val right = roomIndex + 2
    if (from <= left) {
      return burrow.hallway.subList(from + 1, left + 1).all { it == EMPTY }
    } else {
      return burrow.hallway.subList(right, from).all { it == EMPTY }
    }
  }

  val hallwayCoords = listOf(
    Point3i(0, 0, 0),
    Point3i(1, 0, 0),
    Point3i(3, 0, 0),
    Point3i(5, 0, 0),
    Point3i(7, 0, 0),
    Point3i(9, 0, 0),
    Point3i(10, 0, 0),
  )

  val roomCoords = listOf(
    Point3i(2, 1, 0),
    Point3i(4, 1, 0),
    Point3i(6, 1, 0),
    Point3i(8, 1, 0),
  )

  fun cost(hallway: Int, room: Int, cell: Cell, cellIndex: Int): Long {
    val dist = hallwayCoords[hallway].distanceManhattan(roomCoords[room]) + cellIndex

    return (dist.toLong() * cell.energy).also { require(it > 0) }
  }

  fun moveHtoR(burrow: Burrow, from: Int): Pair<Burrow, Long>? {
    val cell = burrow.hallway[from]
    if (cell == EMPTY) {
      return null
    }

    val room = cell.room

    val cellIndex = burrow.rooms[room].cells.indexOfLast { it == EMPTY }

    if (cellIndex == -1) {
      return null
    }

    for (i in cellIndex + 1 until burrow.rooms[room].cells.size) {
      if (burrow.rooms[room].cells[i] != cell) {
        return null
      }
    }

    if (canMove(burrow, from, room)) {
      return burrow.swap(from, room) to cost(from, room, cell, cellIndex)
    }
    return null
  }

  fun moveRtoH(burrow: Burrow, fromRoom: Int, to: Int): Pair<Burrow, Long>? {
    val room = burrow.rooms[fromRoom]

    val cellIndex = burrow.rooms[fromRoom].cells.indexOfFirst { it != EMPTY }
    if (cellIndex == -1) {
      return null
    }

    if (burrow.hallway[to] != EMPTY) {
      return null
    }

    if (!canMove(burrow, to, fromRoom)) {
      return null
    }

    val cell = burrow.rooms[fromRoom].cells[cellIndex]
    if (cell.room == fromRoom) {
      if (burrow.rooms[fromRoom].cells.subList(cellIndex + 1, burrow.rooms[fromRoom].cells.size).all {
          it.room == fromRoom
        }) {
        // all in place
        return null
      }
    }

    return burrow.swap(to, fromRoom) to cost(to, fromRoom, cell, cellIndex)
  }

  fun solve(input: Burrow): Long? {
    val seenStates = mutableMapOf<Burrow, Long>()

    val stack = ArrayDeque<Pair<List<Burrow>, Long>>()
    stack.add(listOf(input) to 0L)

    var best = input to Long.MAX_VALUE

    while (stack.isNotEmpty()) {
      val (burrows, cost) = stack.removeLast()
      val burrow = burrows.last()

      val seenCost = seenStates[burrow]
      if (seenCost != null && seenCost <= cost) {
        // prune this search path
        continue
      }
      seenStates[burrow] = cost

      if (cost > best.second) {
        // prune this search path
        continue
      }

      if (burrow.solved) {
        if (cost < best.second) {
          best = burrow to cost
        }
        continue
      }

      for (index in burrow.hallway.indices) {
        moveHtoR(burrow, index)?.let { (newBurrow, moveCost) ->
          stack.add(burrows + newBurrow to cost + moveCost)
        }
      }

      for (room in burrow.rooms.indices) {
        for (hallway in burrow.hallway.indices) {
          moveRtoH(burrow, room, hallway)?.let { (newBurrow, moveCost) ->
            stack.add(burrows + newBurrow to cost + moveCost)
          }
        }
      }
    }

    return best.second
  }

  override fun part1(input: Burrow): Long? {
    return solve(input.copy(
      rooms = input.rooms.map {
        Room(listOf(it.cells.first(), it.cells.last()))
      }
    ))
  }

  override fun part2(input: Burrow): Long? {
    return solve(input)
  }
}
