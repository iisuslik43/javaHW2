package ru.iisuslik.ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Server that can handle 2 types of requests - list files in directory and download file
 */
public class FTPServer {

  private ServerSocket server;

  /**
   * Create new Server, it doesn't start it
   *
   * @param port in this port server will listen for requests
   * @throws IOException if there is a problem with creating server socket
   */
  public FTPServer(int port) throws IOException {
    this(new ServerSocket(port));
  }

  /**
   * Create new Server, used for tests to mock
   *
   * @param serverSocket socket in which server will work
   */
  public FTPServer(ServerSocket serverSocket) {
    server = serverSocket;
  }

  /**
   * Starts server, it will be waiting for clients
   */
  public void start() {
    while (true) {
      try {
        Socket socket = server.accept();
        Thread t = new Thread(() -> {
          handleRequests(socket);
        });
        t.start();
      } catch (IOException e) {
        System.out.println("Can't connect to client: " + e.getMessage());
        return;
      }
    }
  }

  private static void handleGetRequest(DataInputStream in, DataOutputStream out) throws IOException {
    String path = in.readUTF();
    System.out.println("Get request to get file \"" + path + '\"');
    File file = new File(path);
    if (file.isDirectory()) {
      System.out.println('\"' + path + "\" is a directory");
      out.writeLong(0);
      return;
    }
    out.writeLong(file.length());
    FileInputStream fis = new FileInputStream(file);
    byte[] data = new byte[(int) file.length()];
    fis.read(data);
    fis.close();
    out.write(data);
    out.flush();
    System.out.println("Response for get file request was sent");

  }

  private static void handleListRequest(DataInputStream in, DataOutputStream out) throws IOException {
    String path = in.readUTF();
    System.out.println("Get request to list files in directory \"" + path + '\"');
    File dir = new File(path);
    if (!dir.isDirectory()) {
      System.out.println('\"' + path + "\" is not a directory");
      out.writeInt(0);
      return;
    }
    File[] files = dir.listFiles();
    Arrays.sort(files);
    out.writeInt(files.length);
    for (File file : files) {
      out.writeUTF(file.getName());
      out.writeBoolean(file.isDirectory());
    }
    out.flush();
    System.out.println("Response for list request was sent");
  }

  private static void handleRequests(Socket socket) {
    System.out.println("Start handling requests in thread " + Thread.currentThread().getName());
    try {
      DataInputStream in = new DataInputStream(socket.getInputStream());
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      while (socket.isConnected()) {
        System.out.println("Waiting for next request" + " in thread " + Thread.currentThread().getName());
        int type = in.readInt();
        System.out.println("Get request for type # " + type + " in thread " + Thread.currentThread().getName());
        switch (type) {
          case 1:
            handleListRequest(in, out);
            break;
          case 2:
            handleGetRequest(in, out);
            break;
        }
      }
    } catch (IOException e) {
      System.out.println("Problems with client: " + e.getMessage());
    }
  }

  /**
   * This functions creates and starts server in terminal
   *
   * @param args the first arg should be server's port
   */
  public static void main(String[] args) {
    try {
      FTPServer server = new FTPServer(Integer.parseInt(args[0]));
      server.start();
    } catch (IOException e) {
      System.out.println("Can't create server: " + e.getMessage());
    }

  }

}
