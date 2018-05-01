package redis.embedded.support;

import java.io.File;

public class ServerProvider {

    public static File REDIS_SERVER;
    public static File REDIS_CLI;

    static {
        OsArch2Server.init();
    }

    public static ProcessBuilder createStartProcessBuilder(File redisConf) {
        ProcessBuilder pb = new ProcessBuilder(REDIS_SERVER.getAbsolutePath(), redisConf.getAbsolutePath());
        pb.directory(redisConf.getParentFile());
        return pb;
    }

    public static ProcessBuilder createStopProcessBuilder(int port) {
        ProcessBuilder pb = new ProcessBuilder(REDIS_CLI.getAbsolutePath(), "-p", String.valueOf(port), "shutdown");
        pb.directory(REDIS_CLI.getParentFile());
        return pb;
    }
}
