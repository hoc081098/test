package mmt.aug31

import java.io.DataInputStream
import java.net.Socket

fun main() {
  while (true) {
    val socket = Socket("192.168.10.171", 5000)

    println(socket.inetAddress)
    println(socket.port)
    println(socket.localAddress)
    println(socket.localPort)

    val dis = DataInputStream(socket.getInputStream())
    println(dis.readUTF())
  }
}