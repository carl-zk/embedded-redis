package redis.embedded;

import redis.embedded.support.RedisConfProvider;
import redis.embedded.support.ServerProvider;
import redis.embedded.util.FileUtils;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {
    public static final String REDIS_READY_PATTERN = ".*Redis is starting.*";

    private File redisConf;
    private int port;
    private final boolean isTempServer;

    private Process redisProcess;
    private ExecutorService printErrorExecutor;
    private volatile boolean active = false;

    public RedisServer() {
        this(6379);
    }

    public RedisServer(int port) {
        this(null, port, true);
    }

    public RedisServer(File redisConf, int port) {
        this(redisConf, port, false);
    }

    private RedisServer(File redisConf, int port, boolean isTempServer) {
        this.redisConf = isTempServer ? RedisConfProvider.newRedisConf(FileUtils.createTempDir(), port) : redisConf;
        this.port = port;
        this.isTempServer = isTempServer;
    }

    public synchronized void start() throws EmbeddedRedisException {
        if (active) {
            throw new EmbeddedRedisException("This redis server instance is already running...");
        }
        try {
            redisProcess = ServerProvider.createStartProcessBuilder(redisConf).start();
            startPrintErrors();
            isServerStarted();
            active = true;
            System.out.println("redis server started at port : " + port);
        } catch (IOException e) {
            throw new EmbeddedRedisException("Failed to start redis server ", e);
        }
    }

    public synchronized void stop() throws EmbeddedRedisException {
        if (active) {
            try {
                stopPrintErrors();
                ServerProvider.createStopProcessBuilder(port).start();
                redisProcess.waitFor();
                active = false;
                if (isTempServer) {
                    System.out.println("delete temp path of port " + port + ": " + redisConf.getParentFile().getAbsolutePath());
                    FileUtils.deleteDirectory(redisConf.getParentFile());
                }
                System.out.println("redis server stopped.");
            } catch (IOException | InterruptedException e) {
                throw new EmbeddedRedisException("failed to stop server", e);
            }
        }
    }

    private void isServerStarted() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(redisProcess.getInputStream()))) {
            String outputLine;
            do {
                outputLine = reader.readLine();
                if (outputLine == null) {
                    //Something goes wrong. Stream is ended before server was activated.
                    throw new RuntimeException("Can't start redis server. Check logs for details.");
                }
            } while (!outputLine.matches(REDIS_READY_PATTERN));

        }
    }

    private void startPrintErrors() {
        final InputStream errorStream = redisProcess.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        Runnable printErrorTask = new PrintTask(reader);
        printErrorExecutor = Executors.newSingleThreadExecutor();
        printErrorExecutor.submit(printErrorTask);
    }

    private void stopPrintErrors() {
        if (printErrorExecutor != null && !printErrorExecutor.isShutdown()) {
            printErrorExecutor.shutdown();
        }
    }

    private static class PrintTask implements Runnable {
        private final BufferedReader reader;

        private PrintTask(BufferedReader reader) {
            this.reader = reader;
        }

        public void run() {
            try {
                print();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void print() throws Exception {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } finally {
                reader.close();
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getPort() {
        return port;
    }
}
