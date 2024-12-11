import utils.*

fun main() {
  Day11.run()
}

typealias Day11In = Triple<Set<String>, Map<String, Set<String>>, Set<String>>

object Day11 : Solution<Day11In>() {
  override val name = "day11"
  override val parser: Parser<Day11In> = Parser { it.trim().split("\n\n") }
    .mapItems {
      it.split("\n").drop(1)
    }.map {
      val (friends, dislikes, notInvited) = it
      Triple(
        friends.toSet(),
        dislikes.associate { str ->
          val (person, rest) = str.cut(":")
          person to rest.split(", ").toSet()
        },
        notInvited.toSet(),
      )
    }

  override fun part1(input: Day11In): String {
    val (friends, dislikes, notInvited) = input

    var best = Integer.MIN_VALUE to emptyList<String>()

//    // vale: Liina, Henrik, Jaan, Heljo, Kristjan, Rain, Marko, Risto, Helin, Tiina, Meelis, Margus, Mihkel, Pavel
//    (friends - notInvited).toList().combinations.forEach { guests ->
//      val invited = guests.toSet()
//      val fits = invited.all { invitee ->
//        (dislikes[invitee] ?: emptySet()).none { it in guests }
//      }
//
//      if (fits && invited.size > best.first) {
//        best = invited.size to invited.toList()
//      }
//    }
//    return best.second.joinToString(", ")

    val invited = mutableSetOf<String>()

    friends.forEach { fren ->
      if (fren in notInvited) return@forEach
      if (invited.any { it in (dislikes[fren] ?: emptySet<String>()) }) return@forEach
      if (invited.any { fren in (dislikes[it] ?: emptySet<String>()) }) return@forEach
      invited += fren
    }

    // õige: Liina, Rita, Jaan, Heljo, Kristiina
    return invited.joinToString(", ")
  }
}
