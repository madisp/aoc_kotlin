fun main() {
  val lines = readFile("day2").lines()

  // pairs of (horizontal, depth) deltas
  val deltas = lines.map {
    val (direction, amount) = it.split(" ")
    when (direction) {
      "forward" -> amount.toInt() to 0
      "down" -> 0 to amount.toInt()
      "up" -> 0 to -amount.toInt()
      else -> 0 to 0
    }
  }

  val destination = deltas.reduce { acc, pair ->
    acc.first + pair.first to acc.second + pair.second
  }

  println("first:")
  println(destination.first * destination.second)

  val destination2 = lines.fold(SubmarineState(0, 0, 0)) { state, line ->
    val (direction, amountString) = line.split(" ")
    val amount = amountString.toInt()
    when (direction) {
      "forward" -> state.copy(
        horizontal = state.horizontal + amount,
        depth = state.depth + state.aim * amount
      )
      "down" -> state.copy(aim = state.aim + amount)
      "up" -> state.copy(aim = state.aim - amount)
      else -> throw IllegalStateException("shouldn't happen")
    }
  }

  println("second:")
  println(destination2.horizontal * destination2.depth)
}

data class SubmarineState(val horizontal: Int, val depth: Int, val aim: Int)
