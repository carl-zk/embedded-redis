package redis.embedded.support;

import redis.embedded.enume.OsArch;
import redis.embedded.EmbeddedRedisException;
import redis.embedded.util.FileUtils;
import redis.embedded.util.OsArchitecture;

import java.io.File;
import java.io.IOException;

public class OsArch2Server {

    static {
        try {
            OsArch currentOsArch = OsArch.osArchType(OsArchitecture.currentOsArch());

            File tempDir = FileUtils.createTempDir();
            System.out.println("REDIS_SERVER path: " + tempDir.getAbsolutePath());

            switch (currentOsArch) {
                case UNIX_x86_64:
                case MAC_OS_X_x86_64:
                    ServerProvider.REDIS_SERVER = FileUtils.extractFileFromJar(tempDir, "redis-server-4.0.9");
                    ServerProvider.REDIS_CLI = FileUtils.extractFileFromJar(tempDir, "redis-cli-4.0.9");
                    break;
                default:
                    throw new EmbeddedRedisException("not support this os yet");
            }

            ServerProvider.REDIS_SERVER.setExecutable(true);
            ServerProvider.REDIS_CLI.setExecutable(true);

        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void init() {
    }
}
