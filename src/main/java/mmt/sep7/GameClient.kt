package mmt.sep7

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import kotlin.concurrent.thread


class GameClient : JFrame(), MouseListener {
  private val clickedPoints = mutableSetOf<Point>()
  private val socket = Socket("localhost", 5000)

  override fun mouseReleased(e: MouseEvent?) = Unit

  override fun mouseEntered(e: MouseEvent?) = Unit

  override fun mouseExited(e: MouseEvent?) = Unit

  override fun mousePressed(e: MouseEvent?) = Unit

  override fun mouseClicked(e: MouseEvent) {
    val x = e.x
    val y = e.y
    if (x !in 0..width) {
      return
    }
    if (y !in 0..height) {
      return
    }

    val idx = (x - OFFSET) / SIZE
    val idy = (y - OFFSET) / SIZE

    runCatching {
      val dos = DataOutputStream(socket.getOutputStream())
      dos.writeUTF(idx.toString())
      dos.writeUTF(idy.toString())
    }
  }

  init {
    title = "Caro client"
    size = Dimension(
      NUMBER * SIZE + OFFSET * 2,
      NUMBER * SIZE + OFFSET * 2
    )
    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    addMouseListener(this)
    isVisible = true

    thread {
      while (true) {
        runCatching {
          val dis = DataInputStream(socket.getInputStream())
          val idx = dis.readUTF().toInt()
          val idy = dis.readUTF().toInt()

          SwingUtilities.invokeAndWait {
            clickedPoints.add(Point(idx, idy).also(::println))
            repaint()
          }
        }
      }
    }
  }

  override fun paint(g: Graphics) {
    println("Paint")

    g.color = Color.WHITE
    g.fillRect(0, 0, width, height)

    g.color = Color.BLACK
    (0..NUMBER).forEach {
      g.drawLine(
        OFFSET,
        OFFSET + it * SIZE,
        OFFSET + NUMBER * SIZE,
        OFFSET + it * SIZE
      )
      g.drawLine(
        OFFSET + it * SIZE,
        OFFSET,
        OFFSET + it * SIZE,
        OFFSET + NUMBER * SIZE
      )
    }

    g.font = Font("Arial", Font.BOLD, 24)
    clickedPoints.forEachIndexed { index, point ->
      val x = point.x * SIZE + OFFSET + SIZE / 4
      val y = point.y * SIZE + OFFSET + 3 * SIZE / 4
      if (index % 2 == 0) {
        g.color = Color.RED
        g.drawString("O", x, y)
      } else {
        g.color = Color.BLUE
        g.drawString("X", x, y)
      }
    }
  }
}

fun main() {
  SwingUtilities.invokeLater {
    GameClient()
  }
}