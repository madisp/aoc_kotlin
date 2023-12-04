import utils.Parse
import utils.Parser
import utils.Solution
import utils.Vec4i
import utils.mapItems

fun main() {
  Day15.run(skipTest = false)
}

object Day15 : Solution<List<Day15.Item>>() {
  override val name = "day15"
  override val parser = Parser.lines.mapItems { parseItem(it) }

  const val TARGET_AMOUNT = 100

  @Parse("{kind}: capacity {capacity}, durability {durability}, flavor {flavor}, texture {texture}, calories {calories}")
  data class Item(
    val kind: Kind,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int,
  ) {
    val ingredients = Vec4i(capacity, durability, flavor, texture)
  }

  enum class Kind {
    Frosting, Candy, Butterscotch, Sugar, Cinnamon;
  }

  private fun score(items: List<Item>, amounts: List<Int>): Int {
    val components = items.zip(amounts).map { (item, amount) -> item.ingredients * amount }
      .reduce { a, b -> a + b }
      .coerceAtLeast(Vec4i(0, 0, 0, 0))
    return components.w * components.x * components.y * components.z
  }

  private fun pickIngredients(remaining: Int, number: Int): List<List<Int>> {
    if (remaining == 0) {
      return listOf(List(number) { 0 })
    }
    return (0 .. remaining).flatMap { a ->
      if (number == 1) {
        listOf(listOf(a))
      } else {
        pickIngredients(remaining - a, number - 1).map { listOf(a) + it }
      }
    }
  }

  override fun part1(input: List<Item>): Int {
    val variants = pickIngredients(TARGET_AMOUNT, input.size)
    return variants.maxOf { score(input, it) }
  }

  override fun part2(input: List<Item>): Any? {
    val variants = pickIngredients(TARGET_AMOUNT, input.size)
    return variants.filter {
      input.zip(it).sumOf { (item, amount) -> item.calories * amount } == 500
    }.maxOf { score(input, it) }
  }
}
