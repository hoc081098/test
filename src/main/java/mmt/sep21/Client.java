package mmt.sep21;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

public class Client {
  static class Panel extends JPanel {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 400;
    private static final int OFFSET = 20;
    private static final String FROM = "USD";
    private static final String TO = "VND";

    private final Queue<Double> rates = new LinkedList<>();
    private final DatagramSocket socket = new DatagramSocket();

    Panel() throws SocketException {
      setPreferredSize(new Dimension(WIDTH, HEIGHT));
      setBackground(Color.WHITE);

      new Thread(() -> {
        while (true) {
          try {
            Thread.sleep(50);

            byte[] bytes = ("ExchangeRate" + FROM + "to" + TO).getBytes();
            DatagramPacket sender = new DatagramPacket(
                bytes,
                bytes.length,
                InetAddress.getByName("localhost"),
                5000
            );
            socket.send(sender);


            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String receiveString = new String(
                packet.getData(),
                0,
                packet.getLength()
            );
            System.out.println("Receive: " + receiveString);

            final double rate = Double.parseDouble(receiveString);
            rates.add(rate);
            if (rates.size() * 5 >= WIDTH - 2 * OFFSET) {
              rates.poll();
            }
            System.out.println(rates.size());

            SwingUtilities.invokeAndWait(this::repaint);
          } catch (IOException | InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.WHITE);
      g.drawRect(
          0,
          0,
          WIDTH,
          HEIGHT
      );

      g.setColor(Color.BLACK);
      g.drawLine(
          OFFSET,
          HEIGHT - OFFSET,
          OFFSET,
          OFFSET
      );
      g.drawLine(
          OFFSET - 4,
          OFFSET + 8,
          OFFSET,
          OFFSET
      );
      g.drawLine(
          OFFSET + 4,
          OFFSET + 8,
          OFFSET,
          OFFSET
      );


      g.drawLine(
          OFFSET,
          HEIGHT - OFFSET,
          WIDTH - OFFSET,
          HEIGHT - OFFSET
      );
      g.drawLine(
          WIDTH - OFFSET - 8,
          HEIGHT - OFFSET - 4,
          WIDTH - OFFSET,
          HEIGHT - OFFSET
      );
      g.drawLine(
          WIDTH - OFFSET - 8,
          HEIGHT - OFFSET + 4,
          WIDTH - OFFSET,
          HEIGHT - OFFSET
      );

      g.setFont(new Font("Menlo", Font.BOLD, 16));
      g.drawString(
          TO,
          OFFSET,
          OFFSET
      );
      g.drawString(
          "t",
          WIDTH - OFFSET,
          HEIGHT - OFFSET
      );
      g.drawString(
          FROM,
          WIDTH - 4 * OFFSET,
          OFFSET
      );

      drawChart(g);
    }

    private void drawChart(Graphics g) {
      Queue<Double> rates = new LinkedList<>(this.rates);
      if (rates.isEmpty()) return;

      final double max = rates.stream().mapToDouble(i -> i).max().getAsDouble();
      final double min = rates.stream().mapToDouble(i -> i).min().getAsDouble();
      final double maxY = HEIGHT - 4 * OFFSET;

      // min -> 0
      // y -> ???
      // max ->  maxY

      final int[] xPoints = range(0, rates.size()).map(i -> OFFSET + i * 5).toArray();
      final int[] yPoints = rates.stream().mapToInt(i -> {
        final double y;

        if (min == max) {
          y = maxY;
        } else {
          y = maxY * (i - min) / (max - min);
        }

        return (int) (HEIGHT - 2 * OFFSET - y);
      }).toArray();

      g.setColor(Color.RED);
      g.drawPolyline(xPoints, yPoints, rates.size());
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      final JFrame frame = new JFrame("Demo");
      frame.setLocationRelativeTo(null);
      try {
        frame.add(new Panel());
      } catch (SocketException e) {
        e.printStackTrace();
      }
      frame.pack();
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setVisible(true);
    });
  }
}
