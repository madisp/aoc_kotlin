package parser

enum class TypeType {
  PRIMITIVE, CLASS, ENUM
}

class Type(
  val type: TypeType,
  val name: String,
  val packageName: String? = null,
  val pattern: Node? = null,
  val props: Map<String, Prop> = emptyMap(),
  val genericArguments: List<Type> = emptyList(),
) {
  val fqcn = if (packageName == null) name else "$packageName.$name"

  override fun toString() = fqcn
}

class Prop(
  val pattern: Node? = null,
  val type: Type,
)

private fun StringBuilder.emitParseBody(indent: Int, type: Type, inputExpr: String = "input"): Set<String> {
  val pattern = type.pattern ?: throw IllegalStateException("No pattern defined for $type")

  var tree = pattern
  var part = 0
  var curExpr = inputExpr

  val props = mutableSetOf<String>()

  // walk the tree and emit parsing code
  while (tree !is LeafNode.Empty) {
    when (val node = tree) {
      is LeafNode.Repeat -> {
        emitRepeat(indent, node, curExpr, type)
        props.add(node.field.name)
        // done with parsing
        tree = LeafNode.Empty
      }
      is LeafNode.Field -> {
        emitField(indent, node, curExpr, type)
        props.add(node.name)
        // done with parsing
        tree = LeafNode.Empty
      }
      is Node.Split -> {
        val left = node.left
        val right = node.right
        val i = "  ".repeat(indent)

        if (right is LeafNode.Empty) {
          append("${i}// remove junk '${node.delim}'\n")
          append("${i}val l${part} = ${curExpr}.removeSuffix(\"${node.delim}\")\n")
          curExpr = "l${part}"
          tree = left
        } else {
          // no need to split for empty, will be done below
          if (left !is LeafNode.Empty) {
            append("${i}// split by '${node.delim}'\n")
            append("${i}val (l$part, r$part) = ${curExpr}.split(\"${node.delim}\", limit = 2)\n")
          }

          when (left) {
            is LeafNode.Empty -> {
              append("${i}// remove junk '${node.delim}'\n")
              append("${i}val r${part} = ${curExpr}.removePrefix(\"${node.delim}\")\n")
            }
            is LeafNode.Field -> emitField(indent, left, "l${part}", type).also {
              props.add(left.name)
            }
            is LeafNode.Repeat -> emitRepeat(indent, left, "l${part}", type).also {
              props.add(left.field.name)
            }
          }

          curExpr = "r${part}"
          tree = right
        }

        part++
      }
      is LeafNode.Empty -> {
        /* done */
      }
    }
  }
  return props
}

private fun StringBuilder.emitField(
  indent: Int,
  node: LeafNode.Field,
  inputExpr: String,
  type: Type,
) {
  val i = "  ".repeat(indent)

  append("$i// parse field ${node.name}\n")
  val prop = type.props[node.name] ?: throw IllegalStateException("No prop ${node.name} in $type")
  when (prop.type.fqcn) {
    "kotlin.String" -> {
      append("${i}val ${node.name} = ${inputExpr}.trim()\n")
    }

    "kotlin.Int" -> {
      append("${i}val ${node.name} = ${inputExpr}.trim().toInt()\n")
    }

    "kotlin.Long" -> {
      append("${i}val ${node.name} = ${inputExpr}.trim().toLong()\n")
    }

    // fall back to parser
    else -> {
      when (prop.type.type) {
        TypeType.ENUM -> {
          append("${i}val ${node.name} = ${prop.type.fqcn}.valueOf(${inputExpr}.trim())\n")
        }
        TypeType.CLASS -> {
          append("${i}val ${node.name} = parse${prop.type.name.substringAfterLast('.')}(${inputExpr}.trim())\n")
        }
        else -> {
          throw IllegalStateException("Unsupported parsing type ${prop.type} for $type.${node.name}")
        }
      }
    }
  }
}

private fun StringBuilder.emitRepeat(
  indent: Int,
  node: LeafNode.Repeat,
  inputExpr: String,
  type: Type,
) {
  val i = "  ".repeat(indent)

  val field = node.field.name
  val delim = node.delim

  append("$i// parse repeat $field (delim: '$delim')\n")
  append("${i}val $field = ${inputExpr}.split(\"$delim\")\n")

  val prop = type.props[field] ?: throw IllegalStateException("No prop $field in $type")
  when (prop.type.fqcn) {
    "kotlin.collections.List" -> {
      val argType = prop.type.genericArguments.first()
      append("$i  .filter { it.isNotBlank() }\n")
      when (argType.fqcn) {
        "kotlin.Int" -> {
          append("$i  .map { it.trim().toInt() }\n")
        }
        "kotlin.Long" -> {
          append("$i  .map { it.trim().toLong() }\n")
        }
        "kotlin.String" -> {
          append("$i  .map { it.trim() }\n")
        }
        else -> {
          append("$i  .map { parse${argType.name.substringAfterLast('.')}(it.trim()) }\n")
        }
      }
    }

    "kotlin.collections.Map" -> {
      val mapPattern = prop.pattern
        ?: throw IllegalStateException("No pattern defined for ${node.field.name} of type ${prop.type}, maps need a pattern, e.g. '{key}={value}'!")
      append("$i  .associate { kv -> \n")
      emitParseBody(
        indent = indent + 2,
        type = Type(
          TypeType.CLASS, "\$meta", "\$KeyValuePair", mapPattern, mapOf(
            "key" to Prop(
              type = prop.type.genericArguments.first(),
            ),
            "value" to Prop(
              type = prop.type.genericArguments.last(),
            ),
          )
        ),
        inputExpr = "kv",
      )
      append("$i    key to value\n")
      append("$i  }\n")
    }

    else -> {
      throw IllegalStateException("Repeat target ${prop.type.fqcn} not supported, use List or Map")
    }
  }
}

fun generateParser(type: Type): String {
  return buildString {

    val funName = if ('.' in type.name) {
      val sep = type.name.lastIndexOf('.')
      "${type.name.substring(0, sep)}.parse${type.name.substring(sep + 1)}"
    } else {
      "parse${type.name}"
    }

    append("fun $funName(input: String): $type {\n")

    val parsedProps = emitParseBody(1, type)

    append("  // construct return obj\n")
    append("  return $type(\n")
    type.props.keys.filter { it in parsedProps }.forEach { prop ->
      append("    $prop = $prop,\n")
    }
    append("  )\n")

    append("}")
  }
}

fun main() {
  val game = Type(
    type = TypeType.CLASS,
    pattern = parse("Game {id}: {r '; ' sets}"),
    name = "Game",
    props = mapOf(
      "id" to Prop(
        type = Type(
          type = TypeType.PRIMITIVE,
          packageName = "kotlin",
          name = "Int",
        ),
      ),
      "sets" to Prop(
        type = Type(
          type = TypeType.CLASS,
          packageName = "kotlin.collections",
          name = "List",
          genericArguments = listOf(
            Type(
              type = TypeType.CLASS,
              name = "Set",
              pattern = parse("{r ', ' shown}"),
              props = mapOf(
                "shown" to Prop(
                  pattern = parse("{value} {key}"),
                  type = Type(
                    type = TypeType.CLASS,
                    packageName = "kotlin.collections",
                    name = "Map",
                    genericArguments = listOf(
                      Type(
                        type = TypeType.ENUM,
                        name = "Color",
                      ),
                      Type(
                        type = TypeType.CLASS,
                        packageName = "kotlin",
                        name = "Int",
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    ),
  )

  println(generateParser(game))
  println(generateParser(game.props["sets"]!!.type.genericArguments.first()))
}
