package mmt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
  public static void main(String[] args) throws IOException {
    final ServerSocket serverSocket = new ServerSocket(6000);

    while (true) {
      final Socket socket = serverSocket.accept();

      new Thread(() -> {

        try (
            final DataInputStream reader = new DataInputStream(socket.getInputStream());
            final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
        ) {

          final String s = reader.readUTF();

          Thread.sleep(2000);

          writer.writeUTF("[SERVER 2] " + s);

        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }

      }).start();
    }
  }
}
