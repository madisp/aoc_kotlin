@file:OptIn(ExperimentalUnsignedTypes::class)

import utils.Parser

fun main() {
  Day16Imp.run()
}

object Day16Imp : Solution<UByteArray> {
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
      return readScalar(bitOff, input, version, type)
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
    val packets = mutableListOf<Packet>()
    var off = bitOff + 6 + 1

    if (lengthType == 0) {
      val end = readBits(input, off, 15) + off + 15
      off += 15
      while (off < end) {
        val packet = readPacket(input, off)
        packets.add(packet)
        off += packet.size
      }
    } else {
      val totalPackets = readBits(input, off, 11)
      off += 11
      repeat(totalPackets) {
        val packet = readPacket(input, off)
        packets.add(packet)
        off += packet.size
      }
    }
    return Operator(version, type, off - bitOff, packets)
  }

  private fun readScalar(
    bitOff: Int,
    input: UByteArray,
    version: Int,
    type: Int
  ): Literal {
    var read = 0
    var out = 0L
    var off = bitOff + 6
    while (true) {
      val bits = readBits(input, off, 5)
      off += 5
      read += 5
      out = (out shl 4) + (bits and 0b1111)
      if (bits and 0b00010000 == 0) {
        return Literal(version, type, 6 + read, out)
      }
    }
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
    return versionSum(readPacket(input, 0))
  }

  override fun part2(input: UByteArray): Long {
    return calculate(readPacket(input, 0))
  }
}