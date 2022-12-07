import utils.Parser
import utils.Solution
import utils.badInput

fun main() {
  Day7Imp.run()
}

sealed class Cmd {
  data class ChangeDir(val relative: String): Cmd()
  data class ListFiles(val output: List<FsNode>): Cmd()
}

sealed class FsNode {
  abstract val name: String

  data class DirNode(
    override val name: String,
    var parentDir: DirNode?,
    val files: MutableList<FsNode>


  ) :  FsNode() {
    override fun toString() = "dir $name"
  }

  data class FileNode(
    override val name: String,
    val size: Long
  ) : FsNode() {
    override fun toString() = "file $name (sz = $size)"
  }
}

data class Session(
  val rootDir: FsNode.DirNode,
  var currentDir: FsNode.DirNode
)

object Day7Imp : Solution<List<Cmd>>() {
  override val name = "day7"
  override val parser = Parser { input ->
    val cmds = input.split("$ ").filter { it.isNotBlank() }

    cmds.map { invoke ->
      val (cmd, output) = invoke.split("\n", limit = 2).map { it.trim() }
      when {
        cmd.startsWith("cd") -> Cmd.ChangeDir(cmd.split(" ", limit = 2)[1].trim())
        cmd.startsWith("ls") -> Cmd.ListFiles(
          output.split("\n").map { it.trim() }.map {
            val (p1, p2) = it.split(" ", limit = 2)
            when (p1) {
              "dir" -> FsNode.DirNode(p2, null, mutableListOf())
              else -> FsNode.FileNode(p2, p1.toLong())
            }
          }
        )
        else -> badInput()
      }
    }.drop(1) // always starts with $ cd /
  }

  override fun part1(input: List<Cmd>): Long {
    val root = FsNode.DirNode("/", null, mutableListOf())
    val session = Session(root, root)

    input.forEach {
      execute(session, it)
    }

    var sum = 0L
    walk(session.rootDir) {
      val sz = size(it)
      if (sz <= 100000) {
        sum += sz
      }
    }

    return sum
  }

  override fun part2(input: List<Cmd>): Long {
    val root = FsNode.DirNode("/", null, mutableListOf())
    val session = Session(root, root)

    input.forEach {
      execute(session, it)
    }

    val rootSize = size(session.rootDir)
    val targetToDelete = rootSize - 40000000L

    var bestDirSize = Long.MAX_VALUE

    walk(session.rootDir) {
      val sz = size(it)
      if (sz in targetToDelete until bestDirSize) {
        bestDirSize = sz
      }
    }

    return bestDirSize
  }

  fun walk(dir: FsNode.DirNode, fn: (FsNode.DirNode) -> Unit) {
    fn(dir)
    dir.files.filterIsInstance<FsNode.DirNode>().forEach {
      walk(it, fn)
    }
  }

  fun size(dir: FsNode.DirNode): Long {
    var size = 0L
    for (child in dir.files) {
      when (child) {
        is FsNode.DirNode -> size += size(child)
        is FsNode.FileNode -> size += child.size
      }
    }
    return size
  }

  fun execute(session: Session, cmd: Cmd) {
    val pwd = session.currentDir

    when (cmd) {
      is Cmd.ChangeDir -> {
        when (cmd.relative) {
          ".." -> session.currentDir = pwd.parentDir!!
          else -> {
            val node = pwd.files.firstOrNull { it is FsNode.DirNode && it.name == cmd.relative }
            if (node != null && node is FsNode.DirNode) {
              node.parentDir = pwd // fix parent dir as we didn't have this from parsing
              session.currentDir = node
            } else {
              val newNode = FsNode.DirNode(cmd.relative, pwd, mutableListOf())
              pwd.files.add(newNode)
              session.currentDir = newNode
            }
          }
        }
      }
      is Cmd.ListFiles -> {
        for (node in cmd.output) {
          if (pwd.files.any { it.name == node.name }) {
            continue
          }

          when (node) {
            is FsNode.DirNode -> {
              pwd.files.add(FsNode.DirNode(node.name, pwd, mutableListOf()))
            }
            is FsNode.FileNode -> {
              pwd.files.add(FsNode.FileNode(node.name, node.size))
            }
          }
        }
      }
    }
  }
}
