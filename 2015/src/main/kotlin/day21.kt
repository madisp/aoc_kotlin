import utils.Parse
import utils.Parser
import utils.Solution
import utils.selections

fun main() {
  Day21.run()
}

object Day21 : Solution<Day21.Entity>() {
  override val name = "day21"
  override val parser = Parser { parseEntity(it) }

  @Parse("Hit Points: {hp}\nDamage: {dmg}\nArmor: {armor}")
  data class Entity(
    val hp: Int,
    val dmg: Int,
    val armor: Int,
  )

  enum class Item(
    val cost: Int,
    val dmg: Int,
    val armor: Int,
  ) {
    // weapons
    Dagger(8, 4, 0),
    Shortsword(10, 5, 0),
    Warhammer(25, 6, 0),
    Longsword(40, 7, 0),
    Greataxe(74, 8, 0),
    // armor
    Leather(13, 0, 1),
    Chainmail(31, 0, 2),
    Splintmail(53, 0, 3),
    Bandedmail(75, 0, 4),
    Platemail(102, 0, 5),
    // rings
    Damage1(25, 1, 0),
    Damage2(50, 2, 0),
    Damage3(100, 3, 0),
    Defense1(20, 0, 1),
    Defense2(40, 0, 2),
    Defense3(80, 0, 3),
    // quick hack for nothing
    Nothing(0, 0, 0);

    companion object {
      val weapons = listOf(Dagger, Shortsword, Warhammer, Longsword, Greataxe)
      val armor = listOf(Leather, Chainmail, Splintmail, Bandedmail, Platemail, Nothing)
      val rings = listOf(Damage1, Damage2, Damage3, Defense1, Defense2, Defense3, Nothing, Nothing)
    }
  }

  infix fun Entity.beats(other: Entity): Boolean {
    val myDps = dmg - other.armor
    val otherDps = other.dmg - armor
    var myHp = hp
    var otherHp = other.hp
    while (myHp > 0) {
      otherHp -= myDps
      myHp -= otherDps
      if (otherHp <= 0) {
        return true
      }
    }
    return false
  }

  private fun List<Item>.toEntity(): Entity {
    return fold(Entity(100, 0, 0)) { entity, item ->
      entity.copy(dmg = entity.dmg + item.dmg, armor = entity.armor + item.armor)
    }
  }

  private fun generateOutfits(): Sequence<Pair<Int, Entity>> {
    return sequence {
      Item.weapons.forEach { weap ->
        Item.armor.forEach { armor ->
          Item.rings.selections(2).forEach { rings ->
            val items = listOf(weap, armor) + rings
            val char = items.toEntity()
            yield(items.sumOf { it.cost } to char)
          }
        }
      }
    }
  }

  override fun part1(): Int {
    val boss = input
    return generateOutfits().filter { (_, it) -> it beats boss }.minOf { (cost, _) -> cost }
  }

  override fun part2(): Int {
    val boss = input
    return generateOutfits().filter { (_, it) -> !(it beats boss) }.maxOf { (cost, _) -> cost }
  }
}
