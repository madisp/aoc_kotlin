import utils.Graph
import utils.Parse
import utils.Parser
import utils.Solution

fun main() {
  Day22.run()
}

object Day22 : Solution<Day22.Boss>() {
  override val name = "day22"
  override val parser = Parser { parseBoss(it) }

  @Parse("Hit Points: {hp}\nDamage: {dmg}")
  data class Boss(
    val hp: Int,
    val dmg: Int,
  )

  data class Player(
    val hp: Int,
    val mana: Int,
  )

  data class Effect(
    val damage: Int = 0,
    val armor: Int = 0,
    val regen: Int = 0,
  )

  enum class Spell(
    val mana: Int,
    val damage: Int = 0,
    val heal: Int = 0,
    val cooldown: Int = 1,
    val effect: Effect = Effect(),
  ) {
    MagicMissile(mana = 53, damage = 4),
    Drain(mana = 73, damage = 2, heal = 2),
    Shield(mana = 113, cooldown = 6, effect = Effect(armor = 7)),
    Poison(mana = 173, cooldown = 6, effect = Effect(damage = 3)),
    Recharge(mana = 229, cooldown = 5, effect = Effect(regen = 101)),
    Pass(0),
  }

  data class GameState(
    val turn: Int = 0,
    val activeSpells: Map<Spell, Int> = emptyMap(),
    val boss: Boss,
    val player: Player = Player(hp = 50, mana = 500),
  )

  fun cast(state: GameState, spell: Spell): GameState {
    require(state.turn % 2 == 0) {
      "Cannot cast on boss turn"
    }
    require(spell !in state.activeSpells) {
      "Cannot cast $spell while it's active (cooldown ${state.activeSpells[spell]})"
    }
    return state.copy(
      activeSpells = state.activeSpells + mapOf(spell to spell.cooldown),
      player = state.player.copy(
        mana = state.player.mana - spell.mana,
        hp = state.player.hp + spell.heal,
      ),
      boss = state.boss.copy(
        hp = state.boss.hp - spell.damage,
      )
    )
  }

  fun Map<Spell, Int>.reduceCooldowns(): Map<Spell, Int> {
    return entries.filter { (_, cd) -> cd > 1 }.associate { (spell, cd) -> spell to cd - 1 }
  }

  fun turn(state: GameState): GameState {
    val shield = state.activeSpells.keys.sumOf { it.effect.armor }

    val boss = state.boss.copy(
      hp = state.boss.hp - state.activeSpells.keys.sumOf { it.effect.damage }
    )

    val player = state.player.copy(
      mana = state.player.mana + state.activeSpells.keys.sumOf { it.effect.regen },
      hp = if (state.turn % 2 == 1) {
        state.player.hp - (boss.dmg - shield).coerceAtLeast(0)
      } else state.player.hp
    )

    return state.copy(
      player = player,
      boss = boss,
      turn = state.turn + 1,
      activeSpells = state.activeSpells.reduceCooldowns(),
    )
  }

  override fun part1(): Int {
    val start = GameState(boss = input)

    val g = Graph<GameState, Spell>(
      edgeFn = { state ->
        if (state.player.hp <= 0) {
          // player dead
          emptyList()
        } else if (state.turn % 2 == 1) {
          // boss turn, nothing to do but advance
          listOf(Spell.Pass to turn(state))
        } else {
          (Spell.entries.toSet() - setOf(Spell.Pass) - state.activeSpells.keys).filter { it.mana <= state.player.mana }.map {
            it to turn(cast(state, it))
          }
        }
      },
      weightFn = { it.mana }
    )

    return g.shortestPath(start) { state -> state.boss.hp <= 0 }.first
  }

  override fun part2(): Int {
    val start = GameState(boss = input)

    val g = Graph<GameState, Spell>(
      edgeFn = { state ->
        if (state.player.hp <= ((state.turn + 1) % 2)) {
          // player dead
          emptyList()
        } else if (state.turn % 2 == 1) {
          // boss turn, nothing to do but advance
          listOf(Spell.Pass to turn(state))
        } else {
          (Spell.entries.toSet() - setOf(Spell.Pass) - state.activeSpells.keys).filter { it.mana <= state.player.mana }.map {
            it to turn(cast(state.copy(player = state.player.copy(hp = state.player.hp - 1)), it))
          }
        }
      },
      weightFn = { it.mana }
    )

    return g.shortestPath(start) { state -> state.boss.hp <= 0 && state.player.hp > -1 }.first
  }
}
