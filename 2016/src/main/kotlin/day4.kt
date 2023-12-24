import utils.Parser
import utils.Solution
import utils.mapItems
import utils.withCounts

fun main() {
  Day4.run()
}

object Day4 : Solution<List<Day4.Room>>() {
  override val name = "day4"
  override val parser = Parser.lines.mapItems { parseRoom(it) }

  fun parseRoom(line: String): Room {
    val checksumStart = line.lastIndexOf('[')
    val idStart = line.lastIndexOf('-')
    return Room(
      line.substring(0, idStart),
      line.substring(idStart + 1, checksumStart).toInt(),
      line.substring(checksumStart + 1, line.length - 1),
    )
  }

  data class Room(
    val name: String,
    val id: Int,
    val checksum: String,
  ) {
    fun computeChecksum(): String {
      val chars = name.toCharArray().filter { it != '-' }.withCounts()
      return chars.entries.sortedWith(compareBy({ -it.value }, { it.key })).take(5).joinToString("") { it.key.toString() }
    }

    fun decrypt(): String {
      return name.toCharArray().map {
        if (it == '-') ' ' else {
          'a' + (((it - 'a') + id) % ('z' - 'a' + 1))
        }
      }.joinToString("")
    }

    val valid: Boolean get() = checksum == computeChecksum()
  }

  override fun part1(): Int {
    return input.filter { it.valid }.sumOf { it.id }
  }

  override fun part2(): Int {
    return input.first { it.decrypt() == "northpole object storage" }.id
  }
}
