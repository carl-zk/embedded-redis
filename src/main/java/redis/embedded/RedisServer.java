package redis.embedded;

import redis.embedded.support.OsArch2Server;
import redis.embedded.support.ServerProvider;

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
    private volatile Boolean active = false;

    public RedisServer() {
        this(OsArch2Server.REDIS_CONF, 6379, true);
    }

    public RedisServer(File redisConf, int port, boolean isTempServer) {
        this.redisConf = redisConf;
        this.port = port;
        this.isTempServer = isTempServer;
    }

    public synchronized void start() throws EmbeddedRedisException {
        ServerProvider.start(this);
    }

    public synchronized void stop() throws EmbeddedRedisException {
        ServerProvider.stop(this);
    }

    public void isServerStarted() throws IOException {
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

    public void startPrintErrors() {
        final InputStream errorStream = redisProcess.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        Runnable printErrorTask = new PrintReaderTask(reader);
        printErrorExecutor = Executors.newSingleThreadExecutor();
        printErrorExecutor.submit(printErrorTask);
    }

    public void stopPrintErrors() {
        if (printErrorExecutor != null && !printErrorExecutor.isShutdown()) {
            printErrorExecutor.shutdown();
        }
    }

    private static class PrintReaderTask implements Runnable {
        private final BufferedReader reader;

        private PrintReaderTask(BufferedReader reader) {
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

    public void setActive(Boolean active) {
        this.active = active;
    }

    public File getRedisConf() {
        return redisConf;
    }

    public void setRedisConf(File redisConf) {
        this.redisConf = redisConf;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Process getRedisProcess() {
        return redisProcess;
    }

    public void setRedisProcess(Process redisProcess) {
        this.redisProcess = redisProcess;
    }

    public boolean isTempServer() {
        return isTempServer;
    }

}
