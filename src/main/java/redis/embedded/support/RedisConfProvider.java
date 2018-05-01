package redis.embedded.support;

import redis.embedded.util.FileUtils;

import java.io.*;

public class RedisConfProvider {
    public static final String DEFAULT_REDIS_CONF = "redis.conf";

    public static File newRedisConf(File toDir, int port) {
        try {
            String name = "redis_" + port + ".conf";
            File dest = new File(toDir, name);
            if (dest.exists() && dest.isFile()) {
                System.out.println(name + " exists");
                return dest;
            }
            File raw = FileUtils.extractFileFromJar(toDir, DEFAULT_REDIS_CONF);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(raw), "UTF-8"));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("^port.*")) {
                        line = "port " + port;
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }
            raw.delete();
            System.out.println("create tmp path for port " + port + ": " + dest.getParentFile().getAbsolutePath());
            return dest;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void main(String[] args) {
        RedisConfProvider.newRedisConf(new File("/Users/hero/workspace/embedded-redis"), 10);
        System.out.println("port abc".matches("^port.*"));
    }
}
