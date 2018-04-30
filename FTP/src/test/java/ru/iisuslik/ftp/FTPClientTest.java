package ru.iisuslik.ftp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import ru.iisuslik.ftp.FTPClient.FTPFile;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FTPClientTest {

  /**
   * Checks that list function works
   */
  @Test
  public void listFiles() throws Exception {
    Socket s = mock(Socket.class);
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    List<FTPFile> res = Arrays.asList(new FTPFile("file", false));
    when(s.getInputStream()).thenReturn(getInputStreamWithList(res));
    when(s.getOutputStream()).thenReturn(byteOut);
    FTPClient client = new FTPClient(s);
    assertEquals(res, client.getList("dir"));
    assertEqualsRequests(1, "dir", byteOut.toByteArray());
  }

  /**
   * Checks that downloading file function works
   */
  @Test
  public void getFile() throws Exception {
    Socket s = mock(Socket.class);
    String file = "data";
    when(s.getInputStream()).thenReturn(getInputStreamWithFile(file));
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    when(s.getOutputStream()).thenReturn(byteOut);
    FTPClient client = new FTPClient(s);
    assertEquals(file, new String(client.getFile("file")));
    assertEqualsRequests(2, "file", byteOut.toByteArray());
  }

  private InputStream getInputStreamWithList(List<FTPFile> files) throws IOException {
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(o);
    out.writeInt(files.size());
    for (FTPFile file : files) {
      out.writeUTF(file.name);
      out.writeBoolean(file.isDirectory);
    }
    return new ByteArrayInputStream(o.toByteArray());
  }

  private InputStream getInputStreamWithFile(String fileContent) throws IOException {
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(o);
    byte[] data = fileContent.getBytes();
    out.writeLong(data.length);
    out.write(data);
    return new ByteArrayInputStream(o.toByteArray());
  }


  private void assertEqualsRequests(int type, String path, byte[] data) throws Exception {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
    assertEquals(type, in.readInt());
    assertEquals(path, in.readUTF());
  }

}