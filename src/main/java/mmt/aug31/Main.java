package mmt.aug31;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    scanIP();
  }

  private static void scanIP() {
    final ExecutorService executorService = Executors.newFixedThreadPool(20);
    for (int i = 1; i < 255; i++) {
      int j = i;
      executorService.submit(() -> {
        try {
          final InetAddress[] addresses = InetAddress.getAllByName("192.168.10." + j);
          for (InetAddress address : addresses) {
            System.out.println(address.getHostName() + "/" + address.getHostAddress());
          }
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
      });
    }
    executorService.shutdown();
  }
}
