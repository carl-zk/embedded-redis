package redis.embedded.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public abstract class FileUtils {

    public static void copyFromURL(URL from, File to) throws IOException {
        try (InputStream read = from.openStream();
             FileOutputStream write = new FileOutputStream(to)) {
            byte[] buff = new byte[1024 * 4];
            int len;
            while ((len = read.read(buff)) != -1) {
                write.write(buff, 0, len);
            }
        }
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!Files.isSymbolicLink(directory.toPath())) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            throw new IOException("Unable to delete directory " + directory);
        }
    }

    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                throw new IOException("Unable to delete file: " + file);
            }
        }
    }

    private static File[] verifiedListFiles(final File directory) throws IOException {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    public static File createTempDir() {
        String baseDir = System.getProperty("java.io.tmpdir");
        String baseName = System.currentTimeMillis() + "-";
        for (int i = 0; i <= TEMP_DIR_ATTEMPTS; i++) {
            File tempDir = new File(baseDir, baseName + i);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory (tried "
                + baseName + "0 to " + baseName + TEMP_DIR_ATTEMPTS + ')');
    }

    public static final int TEMP_DIR_ATTEMPTS = 100;

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.io.tmpdir"));
    }
}