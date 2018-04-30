package ru.iisuslik.ftp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Some tests for FTP server
 */
public class FTPServerTest {

  /**
   * Checks that list function works
   */
  @Test
  public void listRequest() throws Exception {
    ServerSocket serverSocket = mock(ServerSocket.class);
    Socket socket = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(socket.getInputStream()).thenReturn(getInputStream(1, "src/test/resources/testDir"));
    when(socket.getOutputStream()).thenReturn(byteOut);
    when(socket.isConnected()).thenReturn(true).thenReturn(false);
    when(serverSocket.accept()).thenReturn(socket).thenThrow(new IOException("Fake"));
    (new FTPServer(serverSocket)).start();
    sleep(100);
    assertEqualsLists(Arrays.asList(new FTPClient.FTPFile("testFile3", false),
        new FTPClient.FTPFile("testFile4", false)),
        byteOut.toByteArray());
  }

  /**
   * Checks that list function works with directories
   */
  @Test
  public void listWithDirRequest() throws Exception {
    ServerSocket serverSocket = mock(ServerSocket.class);
    Socket socket = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(socket.getInputStream()).thenReturn(getInputStream(1, "src/test/resources"));
    when(socket.getOutputStream()).thenReturn(byteOut);
    when(socket.isConnected()).thenReturn(true).thenReturn(false);
    when(serverSocket.accept()).thenReturn(socket).thenThrow(new IOException("Fake"));
    (new FTPServer(serverSocket)).start();
    sleep(100);
    assertEqualsLists(Arrays.asList(new FTPClient.FTPFile("testDir", true),
        new FTPClient.FTPFile("testFile1", false),
        new FTPClient.FTPFile("testFile2", false)),
        byteOut.toByteArray());
  }

  /**
   * Checks that downloading file function works
   */
  @Test
  public void fileRequest() throws Exception {
    ServerSocket serverSocket = mock(ServerSocket.class);
    Socket socket = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(socket.getInputStream()).thenReturn(getInputStream(2, "src/test/resources/testFile1"));
    when(socket.getOutputStream()).thenReturn(byteOut);
    when(socket.isConnected()).thenReturn(true).thenReturn(false);
    when(serverSocket.accept()).thenReturn(socket).thenThrow(new IOException("Fake"));
    (new FTPServer(serverSocket)).start();
    sleep(100);
    assertEqualsFiles("Goodbye",
        byteOut.toByteArray());
  }

  /**
   * Checks that it returns 0 if there is no such directory
   */
  @Test
  public void directoryDoesntExists() throws Exception {
    ServerSocket serverSocket = mock(ServerSocket.class);
    Socket socket = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(socket.getInputStream()).thenReturn(getInputStream(1, "src/kek"));
    when(socket.getOutputStream()).thenReturn(byteOut);
    when(socket.isConnected()).thenReturn(true).thenReturn(false);
    when(serverSocket.accept()).thenReturn(socket).thenThrow(new IOException("Fake"));
    (new FTPServer(serverSocket)).start();
    sleep(100);
    assertEqualsFiles("",
        byteOut.toByteArray());
  }

  /**
   * Checks that it returns 0 if there is no such file
   */
  @Test
  public void fileDoesntExists() throws Exception {
    ServerSocket serverSocket = mock(ServerSocket.class);
    Socket socket = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(socket.getInputStream()).thenReturn(getInputStream(2, "src/kek"));
    when(socket.getOutputStream()).thenReturn(byteOut);
    when(socket.isConnected()).thenReturn(true).thenReturn(false);
    when(serverSocket.accept()).thenReturn(socket).thenThrow(new IOException("Fake"));
    (new FTPServer(serverSocket)).start();
    sleep(100);
    assertEqualsFiles("",
        byteOut.toByteArray());
  }

  private InputStream getInputStream(int type, String path) throws IOException {
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(o);
    out.writeInt(type);
    out.writeUTF(path);
    return new ByteArrayInputStream(o.toByteArray());
  }

  private void assertEqualsLists(List<FTPClient.FTPFile> files, byte[] data) throws IOException {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
    assertEquals(files.size(), in.readInt());
    for (FTPClient.FTPFile file : files) {
      assertEquals(file.name, in.readUTF());
      assertEquals(file.isDirectory, in.readBoolean());
    }
  }

  private void assertEqualsFiles(String fileContent, byte[] data) throws IOException {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
    byte[] file = fileContent.getBytes();
    long size = in.readLong();
    byte[] actual = new byte[(int) size];
    in.read(actual);
    System.out.println(new String(actual));
    assertEquals(file.length, size);
    assertEquals(fileContent, new String(actual));
  }

}