package redis.embedded;

import redis.embedded.util.Architecture;
import redis.embedded.util.FileUtils;
import redis.embedded.util.OS;
import redis.embedded.util.OsArchitecture;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RedisExecProvider {

    private final Map<OsArchitecture, Server2Config> executables = new HashMap<>();

    public static RedisExecProvider defaultProvider() {
        return new RedisExecProvider();
    }

    private RedisExecProvider() {
        initExecutables();
    }

    private void initExecutables() {
        executables.put(OsArchitecture.UNIX_x86_64, Server2Config.DEFAULT);
        executables.put(OsArchitecture.MAC_OS_X_x86_64, Server2Config.DEFAULT);
    }

    public RedisExecProvider override(OS os, String serverFile, String confFile, String cliFile, int port) {
        for (Architecture arch : Architecture.values()) {
            override(os, arch, serverFile, confFile, cliFile, port);
        }
        return this;
    }

    public RedisExecProvider override(OS os, Architecture arch, String serverFile, String confFile, String cliFile, int port) {
        executables.put(new OsArchitecture(os, arch), new Server2Config(serverFile, confFile, cliFile, port));
        return this;
    }

    public Server2Config get() {
        OsArchitecture osArch = OsArchitecture.detect();
        return executables.get(osArch);
    }

    public static class Server2Config {

        public static final Server2Config DEFAULT = buildDefault();

        public File serverFile;
        public File confFile;
        public File cliFile;

        private int port;

        private Server2Config() {
        }

        public Server2Config(String serverFile, String confFile, String cliFile, int port) {
            this.serverFile = new File(serverFile);
            this.confFile = new File(confFile);
            this.cliFile = new File(cliFile);
            this.port = port;
        }


        private static final Server2Config buildDefault() {
            Server2Config sc = new Server2Config();
            File tmpPath = FileUtils.createTempDir();
            try {
                sc.serverFile = extractFileFromJar(tmpPath, "redis-server-4.0.9");
                sc.confFile = extractFileFromJar(tmpPath, "redis.conf");
                sc.cliFile = extractFileFromJar(tmpPath, "redis-cli-4.0.9");
                sc.port = 6379;

                sc.serverFile.setExecutable(true);
                sc.cliFile.setExecutable(true);
                return sc;
            } catch (IOException e) {
                throw new Error(e);
            }
        }

        public int getPort() {
            return port;
        }

        private static final File extractFileFromJar(File toDir, String fileName) throws IOException {
            File tempFile = new File(toDir, fileName);
            URL jarFile = Thread.currentThread().getContextClassLoader().getResource(fileName);
            FileUtils.copyFromURL(jarFile, tempFile);
            return tempFile;
        }
    }
}
