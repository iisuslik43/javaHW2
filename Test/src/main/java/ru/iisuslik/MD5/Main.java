package ru.iisuslik.MD5;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * This class demonstrate that fork join pool faster than one thread
 */
public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        File file = new File("./src/test");
        long time1 = System.currentTimeMillis();

        System.out.println(MD5single.getMD5(file));
        long time2 = System.currentTimeMillis();
        System.out.println("One thread works: " + (time2 - time1));
        System.out.println(MD5ForkJoin.getMD5(file));


        long time3 = System.currentTimeMillis();
        System.out.println("ForkJoinPool works: " + (time3 - time2));

    }
}
