package mmt.sep7

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

data class Point(val x: Int, val y: Int)

class GameOffline : JFrame(), MouseListener {
  private val clickedPoints = mutableSetOf<Point>()

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

    val point = Point(
      (x - OFFSET) / SIZE,
      (y - OFFSET) / SIZE
    )
    if (clickedPoints.add(point)) {
      this.repaint()
    }
  }

  companion object {
    const val OFFSET = 40
    const val NUMBER = 20
    const val SIZE = 30
  }

  init {
    title = "Game co caro"
    size = Dimension(
      NUMBER * SIZE + OFFSET * 2,
      NUMBER * SIZE + OFFSET * 2
    )
    defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    addMouseListener(this)
    isVisible = true
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
  SwingUtilities.invokeLater { GameOffline() }
}