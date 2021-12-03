fun main() {
  val input = readFile("day1")
    .split("\n")
    .map { it.toInt() }

  println("part1:")
  val part1Count = input
    .windowed(2)
    .filter { (first, second) -> second > first }
    .count()
  println(part1Count)

  println("part2:")
  val part2Count = input
    .windowed(3)
    .map { it.sum() }
    .windowed(2)
    .filter { (first, second) -> second > first }
    .count()

  println(part2Count)
}
