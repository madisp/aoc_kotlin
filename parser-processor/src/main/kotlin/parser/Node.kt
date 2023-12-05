package parser

sealed class Node {
  data class Split(val delim: String, val left: LeafNode, val right: Node): Node()
}

sealed class LeafNode : Node() {
  data object Empty : LeafNode()
  data class Field(val name: String): LeafNode()
  data class Repeat(val delim: String, val field: Field): LeafNode()
}

sealed class Token {
  data class Literal(val value: String): Token()
  data class Expression(val contents: String): Token()
}

fun parse(pattern: String): Node {
  return tree(tokenize(pattern))
}

private fun tokenize(pattern: String): List<Token> {
  val tokens = mutableListOf<Token>()
  var index = 0

  while (index < pattern.length) {
    val (token, newIndex) = readToken(pattern, index)
    tokens.add(token)
    index = newIndex
  }
  return tokens
}

private fun tree(tokens: List<Token>): Node {
  if (tokens.isEmpty()) {
    return LeafNode.Empty
  } else if (tokens.size == 1) {
    val token = tokens.single()
    require (token is Token.Expression) { "Expected expression, got $token" }
    return leaf(token)
  } else {
    val firstToken = tokens.first()

    val splitOff = if (firstToken is Token.Expression) 1 else 0

    val delim = tokens[splitOff]

    require(delim is Token.Literal) { "Expected literal, got ${tokens[splitOff]}" }

    val left = if (firstToken is Token.Expression) leaf(firstToken) else LeafNode.Empty
    val right = tree(tokens.drop(splitOff + 1))

    return Node.Split(delim.value, left, right)
  }
}

private fun leaf(token: Token.Expression): LeafNode {
  return if (token.contents.startsWith("r ")) {
    val delimEnd = token.contents.indexOf('\'', 3)
    val delim = token.contents.substring(3, delimEnd)
    val field = token.contents.substring(delimEnd + 2)
    LeafNode.Repeat(delim, LeafNode.Field(field))
  } else {
    LeafNode.Field(token.contents)
  }
}

private val String.escaped: String get() = replace("\n", "\\n")

private fun readToken(input: String, index: Int): Pair<Token, Int> {
  val part = input.substring(index)
  return if (part.startsWith('{')) {
    val end = part.indexOf('}')
    if (end == -1) {
      throw Exception("Unclosed expression starting at $index")
    }
    Token.Expression(part.substring(1, end).escaped) to (index + end + 1)
  } else {
    val end = part.indexOf('{').takeIf { it != -1 } ?: part.length
    Token.Literal(part.substring(0, end).escaped) to (index + end)
  }
}
