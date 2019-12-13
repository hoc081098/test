package mmt.aug31

import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

fun main() {
  val serverSocket = ServerSocket(5000)
  while (true) {
    val socket = serverSocket.accept()
    ProcessSocket(socket).start()
  }
}

class ProcessSocket(private val socket: Socket) : Thread() {
  override fun run() {
    super.run()

    println("Co ket qua: ${socket.inetAddress}")
    val dos = DataOutputStream(socket.getOutputStream())
    dos.writeUTF("Petrus: Current date is ${Date()}")
    socket.close()
  }
}