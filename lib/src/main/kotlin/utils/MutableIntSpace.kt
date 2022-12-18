package utils

class MutableIntSpace(
  private val arr: IntArray,
  val width: Int,
  val height: Int,
  val depth: Int,
) {
  constructor(width: Int, height: Int, depth: Int, valuesFn: (Vec4i) -> Int) :
    this(IntArray(width * height * depth) { index ->
      val z = index / (width * height)
      val planeCoords = index % (width * height)
      valuesFn(Point3i(planeCoords % width, planeCoords / width, z))
    }, width, height, depth)

  operator fun contains(p: Vec4i): Boolean {
    return (p.x in 0 until width && p.y in 0 until height && p.z in 0 until depth)
  }

  operator fun get(p: Vec4i): Int {
    return arr[p.z * width * height + p.y * width + p.x]
  }

  operator fun set(p: Vec4i, value: Int) {
    arr[p.z * width * height + p.y * width + p.x] = value
  }
}
