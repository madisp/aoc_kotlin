import utils.Parser
import utils.Solution

fun main() {
  Day9.run()
}

typealias Day9In = List<Day9.FileNode>

object Day9 : Solution<Day9In>() {
  override val name = "day9"
  override val parser: Parser<Day9In> = Parser { it.trim() }.map { input ->
    buildList {
      var off = 0L
      var cur = 0
      input.forEach { c ->
        val sz = c - '0'
        if (cur % 2 == 0) {
          add(FileNode(cur / 2, off, sz))
        } else {
          add(FileNode(-1, off, sz))
        }
        cur++
        off += sz
      }
    }
  }

  data class FileNode(
    var id: Int,
    val off: Long,
    var sz: Int,
  )

  private val List<FileNode>.checksum: Long get() = sumOf {
    if (it.id != -1) {
      (0 until it.sz).sumOf { b ->
        it.id * (it.off + b)
      }
    } else 0L
  }

  override fun part1(input: Day9In): Long {
    val origFiles = input.map { it.copy() }

    val compact = buildList<FileNode> {
      var startNext = 0
      var endNext = input.size - 1
      var off = 0L
      var lastPartialAdd: FileNode? = null
      while (startNext < endNext) {
        if (input[startNext].id >= 0) {
          // copy file
          lastPartialAdd = null
          add(input[startNext].copy(off = off))
          off += input[startNext].sz
          startNext++
        } else {
          // copy as many chunks from end as possible
          var copied = 0
          while (copied < input[startNext].sz && startNext < endNext) {
            // ignore space at end
            while (input[endNext].id == -1 && startNext < endNext) {
              endNext--
            }
            if (input[endNext].sz <= input[startNext].sz - copied) {
              // fits fully
              lastPartialAdd = null
              add(input[endNext].copy(off = off))
              off += input[endNext].sz
              copied += input[endNext].sz
              endNext--
            } else {
              // fits partly
              val fit = input[startNext].sz - copied
              lastPartialAdd = input[endNext].copy(off = off, sz = fit)
              add(lastPartialAdd)
              input[endNext].sz -= fit
              off += fit
              copied += fit
            }
          }
          startNext++
        }
      }
      if (input[endNext].id >= 0 && input[endNext].sz > 0) {
        if (lastPartialAdd?.id == input[endNext].id) {
          lastPartialAdd.sz += input[endNext].sz
        } else {
          add(input[endNext].copy(off = off))
        }
      }
    }

    origFiles.filter { it.id > 0 }.forEach { orig ->
      if (compact.filter { it.id == orig.id }.sumOf { it.sz } != orig.sz) {
        throw IllegalStateException("File ${orig.id} lost size")
      }
    }

    return compact.checksum
  }

  private fun compactSpace(files: List<FileNode>): List<FileNode> {
    return buildList {
      var curSpace = 0
      var off = 0L
      files.forEach { f ->
        if (f.id == -1) {
          curSpace += f.sz
        } else {
          if (curSpace > 0) {
            add(FileNode(id = -1, sz = curSpace, off = off))
            off += curSpace
            curSpace = 0
          }
          add(f.copy(off = off))
          off += f.sz
        }
      }
    }
  }

  private fun defragFile(files: List<FileNode>, id: Int): List<FileNode> {
    val src = files.indexOfLast { it.id == id }
    for (dst in 0 until src) {
      if (files[dst].id != -1) continue
      if (files[src].sz <= files[dst].sz) {
        return compactSpace(buildList {
          files.indices.forEach { i ->
            if (i == dst) {
              add(files[src].copy(off = files[dst].off))
              if (files[src].sz < files[dst].sz) {
                add(FileNode(id = -1, off = files[dst].off + files[src].sz, sz = files[dst].sz - files[src].sz))
              }
            } else if (i == src) {
              add(files[src].copy(id = -1))
            } else {
              add(files[i])
            }
          }
        })
      }
    }
    return files
  }

  override fun part2(input: Day9In): Long {
    var files = input
    val max = files.maxOf { it.id }

    for (src in max downTo 0) {
      files = defragFile(files, src)
    }

    return files.checksum
  }
}
