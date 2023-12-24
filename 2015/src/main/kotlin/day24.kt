import utils.Parser
import utils.Solution
import utils.mapItems
import utils.selections

fun main() {
  Day24.run()
}

object Day24 : Solution<List<Long>>() {
  override val name = "day24"
  override val parser = Parser.intLines.mapItems { it.toLong() }

  override fun part1(): Long {
    val targetSize = input.sum() / 3

    return (1 .. input.size - 3).firstNotNullOf { firstSize ->
      input.selections(firstSize).filter { sele ->
        sele.sum() == targetSize && (1..input.size - 6).any { secondCount ->
          (input.toSet() - sele.toSet()).toList().selections(secondCount).any { it.sum() == targetSize }
        }
      }.minOfOrNull { it.reduce { a, b -> a * b } }
    }
  }

  override fun part2(): Long {
    val targetSize = input.sum() / 4

    return (1 .. input.size - 4).firstNotNullOf { firstSize ->
      input.selections(firstSize)
        .filter { sele ->
          sele.sum() == targetSize
        }
        .filter { sele ->
          val rest = (input.toSet() - sele.toSet()).toList()
          (1..rest.size).any {
            rest.selections(it)
              .filter { sele2 -> sele2.sum() == targetSize }
              .any { sele2 ->
                val rest2 = (rest.toSet() - sele2.toSet()).toList()
                (1..rest2.size).any {
                  rest2.selections(it).any { sele3 ->
                    sele3.sum() == targetSize
                  }
                }
              }
          }
        }
        .minOfOrNull { it.reduce { a, b -> a * b } }
    }
  }
}
