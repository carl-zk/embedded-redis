package redis.embedded;

import redis.embedded.exceptions.EmbeddedRedisException;
import redis.embedded.RedisExecProvider.Server2Config;
import redis.embedded.util.FileUtils;

import java.io.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class AbstractRedisInstance implements Redis {
    private Server2Config sc;
    private volatile boolean active = false;
    private Process redisProcess;

    private ExecutorService executor;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public synchronized void start() throws EmbeddedRedisException {
        if (active) {
            throw new EmbeddedRedisException("This redis server instance is already running...");
        }
        try {
            redisProcess = createStartProcessBuilder().start();
            logErrors();
            isServerStarted();
            active = true;
        } catch (IOException e) {
            throw new EmbeddedRedisException("Failed to start Redis instance", e);
        }
        System.out.println("redis server started at port : " + getPort());
    }

    @Override
    public synchronized void stop() throws EmbeddedRedisException {
        if (active) {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
            try {
                createStopProcessBuilder().start();
            } catch (Exception e) {
                throw new EmbeddedRedisException("failed to stop server", e);
            }
            tryWaitFor();
            active = false;
            if (sc == Server2Config.DEFAULT) {
                try {
                    FileUtils.deleteDirectory(sc.serverFile.getParentFile());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            System.out.println("redis server stopped.");
        }
    }

    private void logErrors() {
        final InputStream errorStream = redisProcess.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
        Runnable printReaderTask = new PrintReaderRunnable(reader);
        executor = Executors.newSingleThreadExecutor();
        executor.submit(printReaderTask);
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
            } while (!outputLine.matches(redisReadyPattern()));

        }
    }

    protected abstract String redisReadyPattern();

    public void setSc(Server2Config sc) {
        this.sc = sc;
    }

    private ProcessBuilder createStartProcessBuilder() {
        File executable = sc.serverFile;
        ProcessBuilder pb = new ProcessBuilder(sc.serverFile.getAbsolutePath(), sc.confFile.getAbsolutePath());
        pb.directory(executable.getParentFile());
        return pb;
    }

    private ProcessBuilder createStopProcessBuilder() {
        File executable = sc.cliFile;
        ProcessBuilder pb = new ProcessBuilder(sc.cliFile.getAbsolutePath(), "-p", String.valueOf(sc.getPort()), "shutdown");
        pb.directory(executable.getParentFile());
        return pb;
    }

    private void tryWaitFor() {
        try {
            redisProcess.waitFor();
        } catch (InterruptedException e) {
            throw new EmbeddedRedisException("Failed to stop redis instance", e);
        }
    }

    public int getPort() {
        return sc.getPort();
    }

    private static class PrintReaderRunnable implements Runnable {
        private final BufferedReader reader;

        private PrintReaderRunnable(BufferedReader reader) {
            this.reader = reader;
        }

        public void run() {
            try {
                readLines();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        public void readLines() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
