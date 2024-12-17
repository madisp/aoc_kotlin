import kotlin.time.measureTime

// fast version of part 2 that uses
// hand-compiled version of the input for perf
fun findBits(a: Long, output: Int): List<Long> {
  val out = mutableListOf<Long>()
  for (bits in 0b000 .. 0b111) {
    val answ = (a shl 3) + bits
    val b = bits xor 5
    val c = (answ ushr b).toInt()
    val bx = b xor 6
    val bxc = bx xor c
    if (bxc and 0b111 == output) {
      out += answ
    }
  }
  return out
}

fun main() {
  val program = listOf(2,4,1,5,7,5,1,6,0,3,4,2,5,5,3,0).reversed()

  var candidates = listOf(0L)

  val elapsed = measureTime {
    program.forEach { insn ->
      candidates = candidates.flatMap { a ->
        findBits(a, insn)
      }
    }
  }
  println("Best variant in ${elapsed}:")
  println(candidates.min())
}
