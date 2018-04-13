package ru.iisuslik.MD5;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class MD5singleTest {

    private File file1 = new File("./src/test/resources/DIR/file1");
    private File file2 = new File("./src/test/resources/file2");
    private String file1Hash, file2Hash;


    public MD5singleTest() throws IOException, NoSuchAlgorithmException {
        file1Hash = MD5.getFileMD5(file1);
        file2Hash = MD5.getFileMD5(file2);
    }

    @Test
    public void oneFileTest() throws Exception {
        assertEquals(file1Hash, MD5single.getMD5(file1));
        assertEquals(file2Hash, MD5single.getMD5(file2));
    }

    @Test
    public void directoryWithOneFile() throws Exception {
        assertEquals("DIR" + file1Hash, MD5single.getMD5(new File("./src/test/resources/DIR")));
    }

    @Test
    public void emptyDirectory() throws Exception {
        assertEquals("DIRR", MD5single.getMD5(new File("./src/test/resources/DIRR")));
    }

    @Test
    public void recursiveDirectory() throws Exception {
        assertEquals("resourcesDIR" + file1Hash + "DIRR" + file2Hash, MD5single.getMD5(new File("./src/test/resources")));
    }

}