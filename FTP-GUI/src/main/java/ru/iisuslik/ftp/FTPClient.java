package ru.iisuslik.ftp;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Client that can send to FTP server request for list files in directory or download file
 */
public class FTPClient {

  private DataInputStream in;
  private DataOutputStream out;

  /**
   * Create new FTP client
   *
   * @param host FTP server's host
   * @param port FTP server's port
   * @throws IOException if there are some problems with creating socket
   */
  public FTPClient(@NotNull String host, int port) throws IOException {
    this(new Socket(host, port));
  }

  /**
   * Create new FTP client from socket, used for tests to mock
   *
   * @param socket socket that client will use to talk wiith server
   * @throws IOException if there are some problems with getting streams from socket
   */
  public FTPClient(@NotNull Socket socket) throws IOException {
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
  }

  private void sendRequest(int type, @NotNull String path) throws IOException {
    System.out.println("Sending request");
    out.writeInt(type);
    out.writeUTF(path);
    out.flush();
    System.out.println("Stop sending");
  }

  /**
   * Send file list request to FTP server
   *
   * @param path directory to show
   * @return list of files in directory
   * @throws IOException if there are some network problems
   */
  public @NotNull
  List<FTPFile> getList(@NotNull String path) throws IOException {
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

  /**
   * Download file from FTP server
   *
   * @param path file to download
   * @throws IOException if there are some network problems
   */
  public void getFile(@NotNull String path) throws IOException {
    sendRequest(2, path);
    downloadFile(path);
    System.out.println("Downloaded File " + path + " to local file " + fileName(path));
  }

  private String fileName(@NotNull String path) {
    String[] arr = path.split("/");
    return arr[arr.length - 1];
  }

  private void downloadFile(@NotNull String path) throws IOException {
    String fileName = fileName(path);
    long size = in.readLong();
    File fileToSave = new File(fileName);
    fileToSave.createNewFile();
    FileOutputStream fileOut = new FileOutputStream(fileToSave);
    byte[] buffer = new byte[4096];
    int read = 0;
    while (size > 0 && (read = in.read(buffer)) != -1) {
      fileOut.write(buffer, 0, read);
      size -= read;
    }
    fileOut.close();
  }

  /**
   * This functions starts client, print:
   * 1 path/to/dir to list file in dir
   * 2 path/to/file to download file
   *
   * @param args the first arg should be server's hostname, the second - server's port
   */
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
            client.getFile(path);
            break;
        }
      }
    } catch (IOException e) {
      System.out.println("Can't reach server: " + e.getMessage());
    }
  }


  /**
   * Representation of file in server
   */
  public static class FTPFile {

    /**
     * file name
     */
    public String name;

    /**
     * It's true if file is actually a directory
     */
    public boolean isDirectory;

    /**
     * Creates new file representation
     *
     * @param name        file name
     * @param isDirectory is this file a directory
     */
    public FTPFile(@NotNull String name, boolean isDirectory) {
      this.name = name;
      this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
      return (isDirectory ? "dir " : "file ") + name;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof FTPFile) {
        FTPFile other = ((FTPFile) o);
        return name.equals(other.name) && isDirectory == other.isDirectory;
      }
      return false;
    }
  }
}
