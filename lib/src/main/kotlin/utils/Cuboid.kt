package utils

/**
 * A cuboid in 3D space.
 */
data class Cuboid(
  val start: Vec4i,
  val end: Vec4i,
) {
  /**
   * Returns the bottom face of the cuboid.
   */
  val projection: Sequence<Vec2i> get() {
    val (x1, y1, _, _) = start
    val (x2, y2, _, _) = end
    return sequence {
      for (x in minOf(x1, x2) .. maxOf(x1, x2)) {
        for (y in minOf(y1, y2) .. maxOf(y1, y2)) {
          yield(Vec2i(x, y))
        }
      }
    }
  }

  /**
   * Returns all the unit-cubes within the cuboid
   */
  val units: Sequence<Vec4i> get() {
    val (x1, y1, z1, _) = start
    val (x2, y2, z2, _) = end
    return sequence {
      for (x in minOf(x1, x2)..maxOf(x1, x2)) {
        for (y in minOf(y1, y2)..maxOf(y1, y2)) {
          for (z in minOf(z1, z2)..maxOf(z1, z2)) {
            yield(Vec4i(x, y, z, 1))
          }
        }
      }
    }
  }
}
