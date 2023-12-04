import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day16.run()
}

object Day16 : Solution<List<Day16.Sue>>() {
  override val name = "day16"
  override val parser = Parser.lines.mapItems { parseSue(it) }

  @Parse("Sue {id}: {r ', ' items}")
  data class Sue(
    val id: Int,
    @Parse("{key}: {value}")
    val items: Map<ItemKind, Int>
  ) {
    fun matches(blueprint: Map<ItemKind, IntRange>): Boolean {
      return blueprint.all { (k, v) ->
        val mine = items[k]
        mine == null || mine in v
      }
    }
  }

  enum class ItemKind {
    children,
    cats,
    samoyeds,
    pomeranians,
    akitas,
    vizslas,
    goldfish,
    trees,
    cars,
    perfumes;
  }

  override fun part1(input: List<Sue>): Int {
    val blueprint = mapOf(
      ItemKind.children to 3 .. 3,
      ItemKind.cats to 7 .. 7,
      ItemKind.samoyeds to 2 .. 2,
      ItemKind.pomeranians to 3 .. 3,
      ItemKind.akitas to 0 .. 0,
      ItemKind.vizslas to 0 .. 0,
      ItemKind.goldfish to 5 .. 5,
      ItemKind.trees to 3 .. 3,
      ItemKind.cars to 2 .. 2,
      ItemKind.perfumes to 1 .. 1,
    )

    return input.first { it.matches(blueprint) }.id
  }

  override fun part2(input: List<Sue>): Int {
    val blueprint = mapOf(
      ItemKind.children to 3 .. 3,
      ItemKind.cats to 8 .. Integer.MAX_VALUE,
      ItemKind.samoyeds to 2 .. 2,
      ItemKind.pomeranians to (0 until 3),
      ItemKind.akitas to 0 .. 0,
      ItemKind.vizslas to 0 .. 0,
      ItemKind.goldfish to (0 until 5),
      ItemKind.trees to 4 .. Integer.MAX_VALUE,
      ItemKind.cars to 2 .. 2,
      ItemKind.perfumes to 1 .. 1,
    )

    return input.first { it.matches(blueprint) }.id
  }
}
