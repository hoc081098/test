package mmt.sep7

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

fun main() {
  GameServer()
}

const val OFFSET = 40
const val NUMBER = 15
const val SIZE = 30

class GameServer {
  fun sendAll(idx: Int, idy: Int) {
    println("sendAll idx = [$idx], idy = [$idy]")
    processes.forEach {
      val dos = DataOutputStream(it.socket.getOutputStream())
      dos.writeUTF(idx.toString())
      dos.writeUTF(idy.toString())
    }
  }

  val clickedPoints: MutableSet<Point> = Collections.synchronizedSet(mutableSetOf<Point>())
  val processes = Vector<Process>()

  init {
    val serverSocket = ServerSocket(5000)

    val socket1 = serverSocket.accept()
    val process1 = Process(socket1, this)
    processes.add(process1)
    process1.start()

    val socket2 = serverSocket.accept()
    val process2 = Process(socket2, this)
    processes.add(process2)
    process2.start()

    println("Server running...")
  }
}

class Process(
  val socket: Socket,
  private val server: GameServer
) : Thread() {
  override fun run() {
    while (true) {
      runCatching {
        val dis = DataInputStream(socket.getInputStream())
        val idx = dis.readUTF().toInt()
        val idy = dis.readUTF().toInt()

        println("run [$idx, $idy]")

        if (server.clickedPoints.size % 2 == 0 && this === server.processes[1]) {
          return@runCatching
        }
        if (server.clickedPoints.size % 2 == 1 && this === server.processes[0]) {
          return@runCatching
        }

        if (idx < 0 || idx >= NUMBER || idy < 0 || idy >= NUMBER) {
          return@runCatching
        }

        val point = Point(idx, idy)
        if (server.clickedPoints.add(point)) {
          server.sendAll(idx, idy)
        }
      }
    }
  }
}