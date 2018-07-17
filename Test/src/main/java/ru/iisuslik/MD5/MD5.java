package ru.iisuslik.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class with 2 simple functions
 */
public class MD5 {

    /**
     * Counts MD5 from byte array
     *
     * @param md5 array to count
     * @return MD5 hash
     */
    public static String countMD5(byte[] md5) {
        StringBuilder res = new StringBuilder();
        for (byte b : md5) {
            res.append(String.format("%02X", b));
        }
        return res.toString();
    }

    /**
     * Count md5 hash in file, that is really file, not directory
     *
     * @param file file to count(not directory)
     * @return md5 hash of file
     * @throws NoSuchAlgorithmException Throws because of MessageDigest
     * @throws IOException Throws if there is problems with files
     */
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        FileInputStream inputStream = new FileInputStream(file);
        DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
        byte[] buffer = new byte[1024];
        while (digestInputStream.read(buffer) != -1) ;
        MessageDigest digest = digestInputStream.getMessageDigest();
        digestInputStream.close();
        return countMD5(digest.digest());
    }
}
