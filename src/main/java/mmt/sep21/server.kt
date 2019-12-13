package mmt.sep21

import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.random.Random

fun main() {
  val socket = DatagramSocket(5000)

  var map = mapOf(
    "USD" to 1.0,
    "JPY" to 1.0 / 120,
    "VND" to 1.0 / 22500
  )

  while (true) {
    val buffer = ByteArray(1024)
    val packet = DatagramPacket(buffer, buffer.size).also(socket::receive)
    val s = String(
      bytes = packet.data,
      offset = 0,
      length = packet.length
    )
    println("Server receive: $s")

    map = map.mapValues { (_, v) -> v * (1 + (Random.nextDouble() - 0.5) * 0.01) }
    println(map)

    val bytes = runCatching {
      val (_, from, to) = s.split(Regex("(ExchangeRate)|(to)"))
      println("From=$from, to=$to")

      val t1 = map[from] ?: return@runCatching "ERROR"
      val t2 = map[to] ?: return@runCatching "ERROR"

      (t1 / t2).toString()
    }.getOrElse { "ERROR" }.toByteArray()

    DatagramPacket(
      bytes,
      bytes.size,
      packet.address,
      packet.port
    ).let(socket::send)
  }
}