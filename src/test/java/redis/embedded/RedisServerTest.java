package redis.embedded;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import redis.clients.jedis.Jedis;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RedisServerTest {

    private RedisServer redisServer;

    @Before
    public void before() {
        System.out.println("start test...");
    }

    @After
    public void after() {
        redisServer.stop();
    }

    private String value = "AAAAA";

    public String doSomething() {
        redisServer.start();
        Jedis jedis = new Jedis("localhost", redisServer.getPort());
        jedis.set("AAAAA", value);
        String val = jedis.get("AAAAA");
        System.out.println(val);
        return val;
    }

    @Test
    public void emptyConstructor() {
        redisServer = new RedisServer();
        assert value.equals(doSomething());
    }

    @Test
    public void portConstructor() {
        redisServer = new RedisServer(7799);
        assert value.equals(doSomething());
    }

    @Test
    public void confAndPortConstructor() {
        redisServer = new RedisServer(new File("/Users/hero/Downloads/redis/redis.conf"), 6380);
        assert value.equals(doSomething());
    }

    @Test(expected = EmbeddedRedisException.class)
    public void notExistsConfConstructor() {
        redisServer = new RedisServer(new File("/not/exists/redis.conf"), 6380);
        assert value.equals(doSomething());
    }

    @Test(expected = EmbeddedRedisException.class)
    public void shouldNotAllowMultipleRunsWithoutStop() {
        try {
            redisServer = new RedisServer();
            redisServer.start();
            redisServer.start();
        } finally {
            redisServer.stop();
        }
    }

    @Test(expected = EmbeddedRedisException.class)
    public void shouldAllowSubsequentRuns() {
        redisServer = new RedisServer();
        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();
    }

    @Test
    public void shouldIndicateInactiveBeforeStart() throws Exception {
        redisServer = new RedisServer();
        assertFalse(redisServer.isActive());
    }

    @Test
    public void shouldIndicateActiveAfterStart() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();
        assertTrue(redisServer.isActive());
        redisServer.stop();
    }

    @Test
    public void shouldIndicateInactiveAfterStop() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();
        redisServer.stop();
        assertFalse(redisServer.isActive());
    }

    @Test
    public void shouldOverrideDefaultExecutable() throws Exception {
//        ServerProvider customProvider = ServerProvider.defaultTempProvider()
//                .override(OS.UNIX, Architecture.x86, Resources.getResource("redis-server-2.8.19-32").getFile())
//                .override(OS.UNIX, Architecture.x86_64, Resources.getResource("redis-server-2.8.19").getFile())
//                .override(OS.WINDOWS, Architecture.x86, Resources.getResource("redis-server-2.8.19.exe").getFile())
//                .override(OS.WINDOWS, Architecture.x86_64, Resources.getResource("redis-server-2.8.19.exe").getFile())
//                .override(OS.MAC_OS_X, Resources.getResource("redis-server-2.8.19").getFile());
//
//        redisServer = new RedisServerBuilder()
//                .redisExecProvider(customProvider)
//                .build();
    }

    @Test(expected = EmbeddedRedisException.class)
    public void shouldFailWhenBadExecutableGiven() throws Exception {
//        ServerProvider buggyProvider = ServerProvider.defaultTempProvider()
//                .override(OS.UNIX, "some")
//                .override(OS.WINDOWS, Architecture.x86, "some")
//                .override(OS.WINDOWS, Architecture.x86_64, "some")
//                .override(OS.MAC_OS_X, "some");
//
//        redisServer = new RedisServerBuilder()
//                .redisExecProvider(buggyProvider)
//                .build();
    }
}
