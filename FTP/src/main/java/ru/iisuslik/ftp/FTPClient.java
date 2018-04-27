package ru.iisuslik.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FTPClient {

  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;

  public FTPClient(String host, int port) throws IOException {
    socket = new Socket(host, port);
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
  }

  private void sendRequest(int type, String path) throws IOException {
    System.out.println("Sending request");
    out.writeInt(type);
    out.writeUTF(path);
    System.out.println("Stop sending");
  }

  private List<FTPFile> getList(String path) throws IOException {
    sendRequest(1, path);
    ArrayList<FTPFile> files = new ArrayList<>();
    int count = in.readInt();
    for (int i = 0; i < count; i++) {
      String filePath = in.readUTF();
      boolean isDirectory = in.readBoolean();
      files.add(new FTPFile(filePath, isDirectory));
    }
    System.out.println("Get list of " + count + " files in \"" + path + '\"');
    return files;
  }

  private byte[] getFile(String path) throws IOException {
    sendRequest(2, path);
    long size = in.readLong();
    byte[] data = new byte[(int) size];
    in.read(data);
    System.out.println("Get file \"" + path + "\" with size " + size);
    return data;
  }

  public static void main(String[] args) {
    FTPClient client;
    try {
      client = new FTPClient(args[0], Integer.parseInt(args[1]));
    } catch (IOException e) {
      System.out.println("Can't connect to server: " + e.getMessage());
      return;
    }
    Scanner in = new Scanner(System.in);
    try {
      while (true) {
        int type = in.nextInt();
        String path = in.next();
        System.out.println("Send request with type # " + type);
        switch (type) {
          case 1:
            System.out.println(client.getList(path));
            break;
          case 2:
            System.out.println(new String(client.getFile(path)));
            break;
        }
      }
    } catch (IOException e) {
      System.out.println("Can't reach server: " + e.getMessage());
    }
  }


  public static class FTPFile {
    public String name;
    public boolean isDirectory;

    public FTPFile(String name, boolean isDirectory) {
      this.name = name;
      this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
      return (isDirectory ? "dir " : "file ") + name;
    }
  }
}
