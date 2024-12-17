import kotlin.system.exitProcess

// brute-forcing part 2.
// I think this will take around 10 days to run on my computer before it finds the answer.
fun main() {
  val program = intArrayOf(2,4,1,5,7,5,1,6,0,3,4,2,5,5,3,0)

  val expectedOutput = program.map { it.toLong() }.fold(0L) { acc, num ->
    (acc shl 3) + num
  }

  outer@for (aInit in (1 shl 46) until Long.MAX_VALUE) {
    var output = 0L

    var a = aInit
    var b: Long
    var c: Long

    while (a != 0L) {
      // bst 4
      b = a and 7

      // bxl 5
      b = b xor 5

      // cdv 5
      c = (a ushr b.toInt())

      // bxl 6
      b = b xor 6

      // adv 3
      a = (a ushr 3)

      // bxc 2
      b = b xor c

      // oot 5
      output = (output shl 3) + (b and 7)

      // jnz 0
      // loop done by actual while loop
    }


    if (output == expectedOutput) {
      println(aInit)
      exitProcess(0)
    }
  }
}
