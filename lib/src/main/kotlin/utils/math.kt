package utils

import kotlin.math.sqrt

fun solveQuadratic(a: Double, b: Double, c: Double): Pair<Double, Double> {
  val x1 = (-b + sqrt(b * b - 4 * a * c)) / 2 * a
  val x2 = (-b - sqrt(b * b - 4 * a * c)) / 2 * a
  return minOf(x1, x2) to maxOf(x1, x2)
}
