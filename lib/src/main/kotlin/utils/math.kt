package utils

import kotlin.math.sqrt

fun solveQuadratic(a: Double, b: Double, c: Double): Pair<Double, Double> {
  val x1 = (-b + sqrt(b * b - 4 * a * c)) / 2 * a
  val x2 = (-b - sqrt(b * b - 4 * a * c)) / 2 * a
  return minOf(x1, x2) to maxOf(x1, x2)
}

val Int.prime: Boolean get() {
  return (2 .. sqrt(this.toDouble()).toInt() + 1).none {
    this % it == 0
  }
}

// TODO(madis) optimize?
val Int.factors: List<Int> get() {
  val divisor = (2 until this).firstOrNull { this % it == 0 }
  if (divisor == null) return listOf(this)
  return listOf(divisor) + (this / divisor).factors
}

// TODO(madis) optimize?
fun lcm(numbers: List<Int>): Long {
  return numbers.flatMap { it.factors.withCounts().entries }
    .groupBy { (k, _) -> k }
    .map { (k, v) -> k to v.maxOf { it.value } }
    .fold(1L) { acc, (n, e) -> acc * n.toLong().pow(e) }
}
