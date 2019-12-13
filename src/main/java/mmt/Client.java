package mmt;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
  public static void main(String[] args) throws IOException {
    final Socket socket1 = new Socket("localhost", 5000);
    try (
        final DataInputStream reader = new DataInputStream(socket1.getInputStream());
        final DataOutputStream writer = new DataOutputStream(socket1.getOutputStream());
    ) {

      final String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
      writer.writeUTF(s);
      System.out.println(reader.readUTF());

    } catch (IOException e) {
      e.printStackTrace();
    }

    ///
    ///
    ///


    final Socket socket2 = new Socket("localhost", 6000);
    try (
        final DataInputStream reader = new DataInputStream(socket2.getInputStream());
        final DataOutputStream writer = new DataOutputStream(socket2.getOutputStream());
    ) {

      final String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
      writer.writeUTF(s);
      System.out.println(reader.readUTF());

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
