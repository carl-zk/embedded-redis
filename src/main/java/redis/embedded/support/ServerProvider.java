package redis.embedded.support;

import redis.embedded.EmbeddedRedisException;
import redis.embedded.RedisServer;
import redis.embedded.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerProvider {

    public static File REDIS_SERVER;
    public static File REDIS_CLI;

    private static AtomicInteger runningServerNum = new AtomicInteger(0);

    static {
        OsArch2Server.init();
    }

    public static void start(RedisServer server) {
        if (server.isActive()) {
            throw new EmbeddedRedisException("This redis server instance is already running...");
        }
        try {
            Process redisProcess = createStartProcessBuilder(server.getRedisConf()).start();
            server.setRedisProcess(redisProcess);
            server.startPrintErrors();
            server.isServerStarted();
            server.setActive(true);
            runningServerNum.incrementAndGet();
        } catch (IOException e) {
            throw new EmbeddedRedisException("Failed to start Server instance", e);
        }
        System.out.println("redis server started at port : " + server.getPort());
    }

    public static void stop(RedisServer server) {
        if (server.isActive()) {
            server.stopPrintErrors();
            try {
                createStopProcessBuilder(server.getPort()).start();
                server.getRedisProcess().waitFor();
            } catch (Exception e) {
                throw new EmbeddedRedisException("failed to stop server", e);
            }
            server.setActive(false);
            int num = runningServerNum.decrementAndGet();
            if (num == 0) {
                try {
                    System.out.println("delete REDIS_CONF path: " + OsArch2Server.REDIS_CONF.getParentFile().getAbsolutePath());
                    FileUtils.deleteDirectory(OsArch2Server.REDIS_CONF.getParentFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("redis server stopped.");
        }
    }

    private static ProcessBuilder createStartProcessBuilder(File redisConf) {
        ProcessBuilder pb = new ProcessBuilder(REDIS_SERVER.getAbsolutePath(), redisConf.getAbsolutePath());
        pb.directory(redisConf.getParentFile());
        return pb;
    }

    private static ProcessBuilder createStopProcessBuilder(int port) {
        ProcessBuilder pb = new ProcessBuilder(REDIS_CLI.getAbsolutePath(), "-p", String.valueOf(port), "shutdown");
        pb.directory(REDIS_CLI.getParentFile());
        return pb;
    }
}
