@file:OptIn(ExperimentalUnsignedTypes::class)

import utils.Parser

fun main() {
  Day16Func.run()
}

object Day16Func : Solution<UByteArray> {
  override val name = "day16"
  override val parser = Parser { input ->
    input.chunked(2).map { it.toUByte(16) }.toUByteArray()
  }

  sealed interface Packet {
    val version: Int
    val type: Int
    val size: Int
  }

  data class Literal(
    override val version: Int,
    override val type: Int,
    override val size: Int,
    val value: Long
  ) : Packet

  data class Operator(
    override val version: Int,
    override val type: Int,
    override val size: Int,
    val packets: List<Packet>
  ) : Packet

  private fun readBits(input: UByteArray, bitOff: Int, bitLen: Int): Int {
    val byte = input[bitOff / 8]
    val localBitOff = bitOff % 8

    if (localBitOff + bitLen <= 8) {
      val mask = (1 shl bitLen) - 1
      return mask and (byte.toInt() ushr (8 - bitLen - localBitOff))
    } else {
      val len = 8 - localBitOff
      val mask = (1 shl len) - 1
      return ((mask and byte.toInt()) shl (bitLen - len)) + readBits(input, bitOff + len, bitLen - len)
    }
  }

  private fun readPacket(input: UByteArray, bitOff: Int): Packet {
    val version = readBits(input, bitOff, 3)
    val type = readBits(input, bitOff + 3, 3)

    if (type == 4) {
      return readScalar(bitOff + 6, input, version, type)
    } else {
      return readOperator(input, bitOff, version, type)
    }
  }

  private fun readOperator(
    input: UByteArray,
    bitOff: Int,
    version: Int,
    type: Int
  ): Operator {
    // operator
    val lengthType = readBits(input, bitOff + 6, 1)

    if (lengthType == 0) {
      val end = readBits(input, bitOff + 6 + 1, 15) + bitOff + 6 + 1 + 15

      val ps = generateSequence(bitOff + 6 + 1 + 15 to listOf(readPacket(input, bitOff + 6 + 1 + 15))) { (off, ps) ->
          val newOff = off + ps.last().size
          if (newOff >= end) null else newOff to ps + readPacket(input, newOff)
        }
        .map { it.second }
        .last()

      return Operator(version, type, 6 + 1 + 15 + ps.sumOf { it.size }, ps)
    } else {
      val totalPackets = readBits(input, bitOff + 6 + 1, 11)

      val ps = generateSequence(bitOff + 6 + 1 + 11 to listOf(readPacket(input, bitOff + 6 + 1 + 11))) { (off, ps) ->
          val newOff = off + ps.last().size
          newOff to ps + readPacket(input, newOff)
        }
        .map { it.second }
        .take(totalPackets)
        .last()

      return Operator(version, type, 6 + 1 + 11 + ps.sumOf { it.size }, ps)
    }
  }

  private fun readScalar(
    bitOff: Int,
    input: UByteArray,
    version: Int,
    type: Int
  ): Literal {
    val chunks = generateSequence(bitOff) { it + 5 }
      .map { off -> readBits(input, off, 5) }
      .takeWhile { it and 0b00010000 != 0 }
      .map { (it and 0b1111).toLong() }
      .toList()

    val value = (chunks + readBits(input, bitOff + 5 * chunks.size, 5).toLong()).reduce { acc, v -> (acc shl 4) + v }

    return Literal(version, type, 6 + ((chunks.size + 1) * 5), value)
  }

  private fun versionSum(packet: Packet): Int {
    return when (packet) {
      is Literal -> packet.version
      is Operator -> packet.version + packet.packets.sumOf { versionSum(it) }
    }
  }

  private fun calculate(packet: Packet): Long {
    return when (packet) {
      is Literal -> packet.value
      is Operator -> when (packet.type) {
        0 -> packet.packets.sumOf { calculate(it) }
        1 -> packet.packets.fold(1L) { acc, p -> acc * calculate(p) }
        2 -> packet.packets.minOf { calculate(it) }
        3 -> packet.packets.maxOf { calculate(it) }
        5 -> if (calculate(packet.packets[0]) > calculate(packet.packets[1])) 1 else 0
        6 -> if (calculate(packet.packets[0]) < calculate(packet.packets[1])) 1 else 0
        7 -> if (calculate(packet.packets[0]) == calculate(packet.packets[1])) 1 else 0
        else -> throw IllegalStateException("Unknown operator ${packet.type}")
      }
    }
  }

  override fun part1(input: UByteArray): Int {
    return versionSum(readPacket(input, 0)).also {
      if (it !in setOf(8, 965)) throw IllegalStateException("Borken answer $it")
    }
  }

  override fun part2(input: UByteArray): Long {
    return calculate(readPacket(input, 0)).also {
      if (it !in setOf(54L, 116672213160L)) throw IllegalStateException("Borken answer $it")
    }
  }
}