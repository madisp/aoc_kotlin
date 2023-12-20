import utils.Parser
import utils.Solution
import utils.cut
import utils.lcm
import utils.mapItems
import java.util.LinkedList

fun main() {
  Day20.run(skipTest = true)
}

object Day20 : Solution<Map<String, Day20.Module>>() {
  override val name = "day20"
  override val parser = Parser.lines
    .mapItems { Module.parse(it) }
    .map { it.associateBy { m -> m.name } + mapOf("button" to Module.Broadcast("button", listOf("broadcaster"))) }

  private fun Pair<Long, Long>.addSig(high: Boolean, amount: Long): Pair<Long, Long> {
    return if (high) first to second + amount else first + amount to second
  }

  sealed class Module {
    abstract val name: String
    abstract val targets: List<String>
    var connected = mutableSetOf<String>()

    abstract fun send(modules: Map<String, Module>, src: String, high: Boolean): Boolean?

    open fun connectSrc(modules: Map<String, Module>, src: String) {
      if (src in connected) {
        return
      }
      connected += src
      targets.map { modules[it] }.forEach { it?.connectSrc(modules, this.name) }
    }

    data class FlipFlop(
      override val name: String,
      override val targets: List<String>,
    ) : Module() {
      var pulse = false

      override fun send(modules: Map<String, Module>, src: String, high: Boolean): Boolean? {
        if (high) {
          return null
        }

        pulse = !pulse
        return pulse
      }
    }

    data class And(
      override val name: String,
      override val targets: List<String>,
    ) : Module() {
      var mem = mutableMapOf<String, Boolean>()

      override fun send(modules: Map<String, Module>, src: String, high: Boolean): Boolean {
        mem[src] = high
        return mem.values.any { !it }
      }

      override fun connectSrc(modules: Map<String, Module>, src: String) {
        mem.putIfAbsent(src, false)
        super.connectSrc(modules, src)
      }
    }

    data class Broadcast(
      override val name: String,
      override val targets: List<String>,
    ) : Module() {
      override fun send(modules: Map<String, Module>, src: String, high: Boolean): Boolean {
        return high
      }
    }

    companion object {
      fun parse(line: String): Module {
        val (module, targetStrs) = line.cut(" -> ")
        val targets = targetStrs.split(", ")
        val name = module.removePrefix("%").removePrefix("&")
        return when (module[0]) {
          '%' -> FlipFlop(name, targets)
          '&' -> And(name, targets)
          else -> Broadcast(name, targets)
        }
      }
    }
  }

  override fun part1(): Long {
    val bc = input["broadcaster"]!!
    bc.connectSrc(input, "button")

    val pulses = LinkedList<Triple<String, String, Boolean>>()

    var sent = 0L to 0L

    val times = 1000

    repeat (times) {
      pulses.add(Triple("button", "broadcaster", false))
      while (pulses.isNotEmpty()) {
        val (src, dst, high) = pulses.removeFirst()
        sent = sent.addSig(high, 1L)
        val module = input[dst] ?: continue

        val send = module.send(input, src, high)
        if (send != null) {
          module.targets.forEach {
            pulses.addLast(Triple(dst, it, send))
          }
        }
      }
    }

    return sent.first * sent.second
  }

  override fun part2(): Long {
    val bc = input["broadcaster"]!!
    bc.connectSrc(input, "button")

    // determined from input by hand:D
    // val counters = listOf("qb", "nn", "rd", "rl")
    // TODO(madis) can we do better here
    val counters = input.values
      .filterIsInstance<Module.And>()
      .filter { it.connected.all { src -> input[src] is Module.FlipFlop } }
    val periods = mutableMapOf<String, Int>()

    val pulses = LinkedList<Triple<String, String, Boolean>>()
    var count = 0L

    outer@while (true) {
      pulses.add(Triple("button", "broadcaster", false))
      count++
      while (pulses.isNotEmpty()) {
        val (src, dst, high) = pulses.removeFirst()

        if (dst == "rx" && !high) {
          break@outer
        }

        val module = input[dst] ?: continue

        val send = module.send(input, src, high)
        if (send != null) {
          module.targets.forEach {
            pulses.addLast(Triple(dst, it, send))
          }
        }

        for (counter in counters) {
          if (periods[counter.name] == null && counter.mem.values.all { it }) {
            periods[counter.name] = count.toInt() // count will be low at this point
          }
          if (periods.size == counters.size) {
            // all periods gathered, stop
            break@outer
          }
        }
      }
    }

    return lcm(periods.values.toList())
  }
}
