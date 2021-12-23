import Day23.Cell.EMPTY
import utils.Parser
import utils.Point3i

fun main() {
  Day23.run()
}

object Day23 : Solution<Day23.Burrow> {
  override val name = "day23"
  override val parser = Parser.lines.map { lines ->
    val uppers = lines[2].substring(3, 10).split('#')
      .map { Cell.valueOf(it) }
    val lowers = lines[3].substring(3, 10).split('#')
      .map { Cell.valueOf(it) }

    val rooms = uppers.zip(lowers) { u, l -> Room(u, l) }

    Burrow(List(size = 7) { EMPTY }, rooms)
  }

  data class Burrow(
    val hallway: List<Cell>, // always size=7
    val rooms: List<Room>, // always size=4
  ) {

    val solved: Boolean get() {
      for (room in rooms.indices) {
        if (rooms[room].upper.room != room || rooms[room].lower.room != room) {
          return false
        }
      }
      return true
    }

    fun swap(hallwayIndex: Int, roomIndex: Int, upper: Boolean): Burrow {
      val roomCell = if (upper) rooms[roomIndex].upper else rooms[roomIndex].lower
      val hallwayCell = hallway[hallwayIndex]

      if (upper) {
        require(hallway[hallwayIndex] == EMPTY || rooms[roomIndex].upper == EMPTY) { "Can't swap if one of the places isn't empty" }
      } else {
        require(hallway[hallwayIndex] == EMPTY || rooms[roomIndex].lower == EMPTY) { "Can't swap if one of the places isn't empty" }
      }

      return copy(
        hallway = List(size = 7) { if (it == hallwayIndex) roomCell else hallway[it] },
        rooms = List(size = 4) {
          if (it == roomIndex) {
            if (upper) {
              rooms[it].copy(upper = hallwayCell)
            } else {
              rooms[it].copy(lower = hallwayCell)
            }
          } else rooms[it]
        }
      )
    }

    override fun toString(): String {
      return buildString {
        append("#############\n")
        append("#${hallway[0]}${hallway[1]}.${hallway[2]}.${hallway[3]}.${hallway[4]}.${hallway[5]}${hallway[6]}#\n")
        append("###")
        for (room in rooms) {
          append("${room.upper}#")
        }
        append("##\n")
        append("  #")
        for (room in rooms) {
          append("${room.lower}#")
        }
        append("  \n")
        append("  #########  ")
      }
    }
  }

  data class Room(
    val upper: Cell,
    val lower: Cell
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

  fun cost(hallway: Int, room: Int, cell: Cell, lower: Boolean): Long {
    val dist = hallwayCoords[hallway].distanceManhattan(roomCoords[room]) +
        if (lower) 1 else 0

    return (dist.toLong() * cell.energy).also { require(it > 0) }
  }

  fun moveHtoR(burrow: Burrow, from: Int): Pair<Burrow, Long>? {
    val cell = burrow.hallway[from]
    if (cell == EMPTY) {
      return null
    }

    val room = cell.room

    if (burrow.rooms[room].upper != EMPTY) {
      return null
    }

    if (burrow.rooms[room].lower != EMPTY && burrow.rooms[room].lower != cell) {
      return null
    }

    if (canMove(burrow, from, room)) {
      return burrow.swap(from, room, burrow.rooms[room].lower != EMPTY) to
          cost(from, room, cell, burrow.rooms[room].lower == EMPTY)
    }
    return null
  }

  fun moveRtoH(burrow: Burrow, fromRoom: Int, to: Int): Pair<Burrow, Long>? {
    val room = burrow.rooms[fromRoom]
    if (room.upper == EMPTY && room.lower == EMPTY) {
      return null
    }

    if (burrow.hallway[to] != EMPTY) {
      return null
    }

    if (!canMove(burrow, to, fromRoom)) {
      return null
    }

    if (room.upper == EMPTY) {
      if (room.lower.room == fromRoom) {
        // already in place
        return null
      }
    } else {
      if (room.upper.room == fromRoom && room.lower.room == fromRoom) {
        // already in place
        return null
      }
    }

    if (room.upper == EMPTY) {
      return burrow.swap(to, fromRoom, false) to cost(to, fromRoom, room.lower, true)
    } else {
      return burrow.swap(to, fromRoom, true) to cost(to, fromRoom, room.upper, false)
    }
  }

//  val states = mutableMapOf<Burrow, Long>()

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

//      count++
//      if (count % 10000 == 0L) {
//        println(seenStates.size)
//      }

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

  override fun part1(input: Burrow): Number? {
//
//    var burrow = input
//    println("$burrow\n")
//
//    var cost = 0L
//
//    burrow = moveRtoH(burrow, 2, 2)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveRtoH(burrow, 1, 3)!!.also { (_, c) -> cost += c }.first
//    burrow = moveHtoR(burrow, 3)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveRtoH(burrow, 1, 3)!!.also { (_, c) -> cost += c }.first
//    burrow = moveHtoR(burrow, 2)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveRtoH(burrow, 0, 2)!!.also { (_, c) -> cost += c }.first
//    burrow = moveHtoR(burrow, 2)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveRtoH(burrow, 3, 4)!!.also { (_, c) -> cost += c }.first
//    burrow = moveRtoH(burrow, 3, 5)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveHtoR(burrow, 4)!!.also { (_, c) -> cost += c }.first
//    burrow = moveHtoR(burrow, 3)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//
//    burrow = moveHtoR(burrow, 5)!!.also { (_, c) -> cost += c }.first
//    println("$burrow\n")
//    println("${burrow.solved}, cost $cost")
//    return null

    return solve(input)
  }
}
