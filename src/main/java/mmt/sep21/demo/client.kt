package mmt.sep21.demo

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main() {
  val socket = DatagramSocket()

  while (true) {
    print("Enter the string: ")
    val s = readLine() ?: continue
    val bytes = s.toByteArray()

    DatagramPacket(
      bytes,
      bytes.size,
      InetAddress.getByName("localhost"),
      5000
    ).let(socket::send)

    val buffer = ByteArray(1024)
    val packet = DatagramPacket(buffer, buffer.size).also(socket::receive)
    val receiveString = String(
      bytes = packet.data,
      offset = 0,
      length = packet.length
    )
    println("Receive: $receiveString")
  }
}