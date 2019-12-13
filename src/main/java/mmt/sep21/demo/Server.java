package mmt.sep21.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
  public static void main(String[] args) throws IOException {
    final DatagramSocket socket = new DatagramSocket(5000);

    while (true) {
      final byte[] buffer = new byte[1024];
      final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      socket.receive(packet);

      final String s = new String(
          packet.getData(),
          0,
          packet.getLength()
      );
      System.out.println("Server receive: " + s);

      final byte[] bytes = s.toUpperCase().getBytes();
      final DatagramPacket sender = new DatagramPacket(
          bytes,
          bytes.length,
          packet.getAddress(),
          packet.getPort()
      );
      socket.send(sender);
    }
  }
}
