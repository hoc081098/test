package mmt.sep21

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import kotlin.concurrent.thread


class Panel : JPanel() {
  companion object {
    const val WIDTH = 700
    const val HEIGHT = 400
    const val OFFSET = 20

    const val FROM = "USD"
    const val TO = "VND"
  }

  private val rates: Queue<Float> = LinkedList<Float>()
  private val socket = DatagramSocket()

  init {
    preferredSize = Dimension(WIDTH, HEIGHT)
    background = Color.WHITE

    thread {
      while (true) {
        runCatching {
          Thread.sleep(50)

          val bytes = "ExchangeRate${FROM}to$TO".toByteArray()

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

          rates += receiveString.toFloatOrNull() ?: return@runCatching
          if (rates.size * 5 >= WIDTH - 2 * OFFSET) {
            rates.poll()
          }
          println(rates.size)

          SwingUtilities.invokeAndWait { repaint() }
        }
      }
    }
  }

  override fun paintComponent(g: Graphics) {
    super.paintComponent(g)

    g.color = Color.WHITE
    g.drawRect(
      0,
      0,
      WIDTH,
      HEIGHT
    )

    g.color = Color.BLACK
    g.drawLine(
      OFFSET,
      HEIGHT - OFFSET,
      OFFSET,
      OFFSET
    )
    g.drawLine(
      OFFSET - 4,
      OFFSET + 8,
      OFFSET,
      OFFSET
    )
    g.drawLine(
      OFFSET + 4,
      OFFSET + 8,
      OFFSET,
      OFFSET
    )


    g.drawLine(
      OFFSET,
      HEIGHT - OFFSET,
      WIDTH - OFFSET,
      HEIGHT - OFFSET
    )
    g.drawLine(
      WIDTH - OFFSET - 8,
      HEIGHT - OFFSET - 4,
      WIDTH - OFFSET,
      HEIGHT - OFFSET
    )
    g.drawLine(
      WIDTH - OFFSET - 8,
      HEIGHT - OFFSET + 4,
      WIDTH - OFFSET,
      HEIGHT - OFFSET
    )

    g.font = Font("Menlo", Font.BOLD, 16)
    g.drawString(
      TO,
      OFFSET,
      OFFSET
    )
    g.drawString(
      "t",
      WIDTH - OFFSET,
      HEIGHT - OFFSET
    )
    g.drawString(
      FROM,
      WIDTH - 4 * OFFSET,
      OFFSET
    )

    drawChart(g)
  }

  private fun drawChart(g: Graphics) {
    g.color = Color.RED

    val max = rates.max() ?: return
    val min = rates.min() ?: return
    val maxY = HEIGHT - 4 * OFFSET

    // min -> 0
    // y -> ???
    // max ->  maxY

    val xPoints = rates.indices.map { OFFSET + it * 5 }.toIntArray()
    val yPoints = rates.map {
      val y = if (max == min) {
        maxY.toFloat()
      } else {
        maxY * (it - min) / (max - min)
      }
      (HEIGHT - 2 * OFFSET - y).toInt()
    }.toIntArray()

    g.drawPolyline(xPoints, yPoints, rates.size)

    /*rates.zipWithNext().forEachIndexed { index, (prev, cur) ->
      g.drawLine(
        OFFSET + (index + 1 - 1) * 5,
        (HEIGHT - OFFSET - prev * 10).toInt(),
        OFFSET + (index + 1) * 5,
        (HEIGHT - OFFSET - cur * 10).toInt()
      )
    }*/
  }
}

fun main() {
  SwingUtilities.invokeLater {
    JFrame().run {
      setLocationRelativeTo(null)
      add(Panel())
      pack()
      defaultCloseOperation = EXIT_ON_CLOSE
      isVisible = true
    }
  }


}