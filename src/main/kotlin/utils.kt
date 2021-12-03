object Utils

fun readFile(name: String): String {
  return Utils.javaClass.getResourceAsStream("/$name.txt").readBytes().toString(Charsets.UTF_8)
}
