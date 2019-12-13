package mmt.sep21;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
  public static void main(String[] args) throws IOException {
    final DatagramSocket socket = new DatagramSocket(5000);
    Map<String, Double> map = new HashMap<>();/*Map.of(
        "USD", 1.0,
        "JPY", 1.0 / 120,
        "VND", 1.0 / 22500
    );*/

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

      final HashMap<String, Double> newMap = new HashMap<>();
      for (Map.Entry<String, Double> entry : map.entrySet()) {
        String k = entry.getKey();
        Double v = entry.getValue();
        newMap.put(k, v * (1 + (new Random().nextDouble() - 0.5) * 0.01));
      }
      System.out.println(newMap);
      map = newMap;

      final String[] strings = s.split("(ExchangeRate)|(to)");
      final String from = strings[1];
      final String to = strings[2];
      System.out.println("From=" + from + ",to=" + to);

      final Double t1 = map.get(from);
      final Double t2 = map.get(to);

      final String result;
      if (t1 != null && t2 != null) {
        result = String.valueOf(t1 / t2);
      } else {
        result = "ERROR";
      }

      final byte[] bytes = result.getBytes();
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
