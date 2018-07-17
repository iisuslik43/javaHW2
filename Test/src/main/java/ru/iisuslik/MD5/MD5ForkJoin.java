package ru.iisuslik.MD5;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Class to calculate MD5 hash of files or directories with fork join pool
 */
public class MD5ForkJoin {
    /**
     * In ForkJoinPool recursively count MD5 of directory or file, if directory has many files, they hash will be count
     * in alphabet order
     *
     * @param file File or directory to count MD5
     * @return MD5 hash
     */
    public static String getMD5(File file) throws NoSuchAlgorithmException, IOException {
        ForkJoinPool pool = new ForkJoinPool(4);
        String res = pool.invoke(new MD5Counter(file));
        pool.shutdown();
        return res;
    }


    private static class MD5Counter extends RecursiveTask<String> {

        private File file;

        private MD5Counter(File file) {
            this.file = file;
        }

        @Override
        protected String compute() {
            if (file.isDirectory()) {
                StringBuilder sb = new StringBuilder();
                sb.append(file.getName());
                List<MD5Counter> subTasks = new LinkedList<>();

                List<File> files = Arrays.asList(file.listFiles());
                files.sort(Comparator.comparing(File::getName));
                for (File f : files) {
                    MD5Counter counter = new MD5Counter(f);
                    counter.fork();
                    subTasks.add(counter);
                }
                for (MD5Counter counter : subTasks) {
                    sb.append(counter.join());
                }
                return sb.toString();
            } else {
                try {
                    return MD5.getFileMD5(file);
                } catch (Exception e) {
                    completeExceptionally(e);
                }
            }
            return "";
        }
    }
}
