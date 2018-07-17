package ru.iisuslik.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static ru.iisuslik.MD5.MD5.getFileMD5;

/**
 * Class to calculate MD5 hash of files or directories in one thread
 */
public class MD5single {
    /**
     * In one thread recursively count MD5 of directory or file, if directory has many files, they hash will be count
     * in alphabet order
     *
     * @param file File or directory to count MD5
     * @return MD5 hash
     */
    public static String getMD5(File file) throws NoSuchAlgorithmException, IOException {
        StringBuilder sb = new StringBuilder();
        if (file.isDirectory()) {
            sb.append(file.getName());
            List<File> files = Arrays.asList(file.listFiles());
            files.sort(Comparator.comparing(File::getName));
            for (File f : files) {
                sb.append(getMD5(f));
            }
        } else {
            sb.append(getFileMD5(file));
        }
        return sb.toString();
    }
}
