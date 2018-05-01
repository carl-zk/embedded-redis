package redis.embedded;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerServerTest {

    private RedisServer redisServer;

    @Test//(timeout = 1500L)
    public void testSimpleRun() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();
        Jedis jedis = new Jedis("localhost", redisServer.getPort());
        jedis.set("a", "a");
        System.out.println(jedis.get("a"));
        TimeUnit.SECONDS.sleep(1);
        redisServer.stop();
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAllowMultipleRunsWithoutStop() throws Exception {
        try {
            redisServer = new RedisServer();
            redisServer.start();
            redisServer.start();
        } finally {
            redisServer.stop();
        }
    }

    @Test
    public void shouldAllowSubsequentRuns() throws Exception {
        redisServer = new RedisServer();
        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();
    }

    @Test
    public void testSimpleOperationsAfterRun() throws Exception {
//		redisServer = new RedisServer();
//		redisServer.start();
//
//		JedisPool pool = null;
//		Jedis jedis = null;
//		try {
//			pool = new JedisPool("localhost", 6379);
//			jedis = pool.getResource();
//			jedis.mset("abc", "1", "def", "2");
//
//			assertEquals("1", jedis.mget("abc").get(0));
//			assertEquals("2", jedis.mget("def").get(0));
//			assertEquals(null, jedis.mget("xyz").get(0));
//		} finally {
//			if (jedis != null)
//				pool.returnResource(jedis);
//			redisServer.stop();
//		}
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
