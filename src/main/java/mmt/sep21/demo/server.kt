package mmt.sep21.demo

import java.net.DatagramPacket
import java.net.DatagramSocket

fun main() {
  val socket = DatagramSocket(5000)

  while (true) {
    val buffer = ByteArray(1024)
    val packet = DatagramPacket(buffer, buffer.size).also(socket::receive)
    val s = String(
      bytes = packet.data,
      offset = 0,
      length = packet.length
    )
    println("Server receive: $s")

    val bytes = s.toUpperCase().toByteArray()
    DatagramPacket(
      bytes,
      bytes.size,
      packet.address,
      packet.port
    ).let(socket::send)
  }
}