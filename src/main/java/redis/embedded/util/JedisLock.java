package redis.embedded.util;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * Stateless Redis Lock
 * <p>
 * examples:
 * 1. tryLock
 * <p>
 * boolean got = JedisLock.tryLock(key, token, time, TimeUnit, jedis);
 * if (got) {
 * try {
 * // do something
 * } finally {
 * JedisLock.unlock(key, token, jedis);
 * }
 * }
 * <p>
 * 2. lock
 * <p>
 * JedisLock.lock(key, token, jedis);
 * try {
 * <p>
 * } finally {
 * JedisLock.unlock(key, token, jedis);
 * }
 */
public class JedisLock {
    /**
     * @param key
     * @param token
     * @param jedis
     */
    public static void unlock(String key, String token, Jedis jedis) {
        if (token.equals(jedis.get(key))) {
            jedis.del(key);
        }
    }

    /**
     * @param key
     * @param token
     * @param jedis
     * @return
     */
    public static void lock(String key, String token, Jedis jedis) {
        isValidKey(key, jedis);
        while (!tryLock(key, token, jedis)) {
            try {
                TimeUnit.MILLISECONDS.sleep(INTERVAL_IN_MILLIS);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void isValidKey(String key, Jedis jedis) {
        long ttl = jedis.ttl(key);
        if (ttl == -1) {
            String val = jedis.get(key);
            throw new Error("redis lock encounter an unexpired key : " + key + ", value : " + val);
        }
    }

    private static final int INTERVAL_IN_MILLIS = 1;

    /**
     * @param key
     * @param token
     * @param timeout
     * @param timeUnit
     * @param jedis
     * @return
     */
    public static boolean tryLock(String key, String token, long timeout, TimeUnit timeUnit, Jedis jedis) {
        timeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        boolean got = tryLock(key, token, jedis);

        while (!got && timeout > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                return false;
            }
            got = tryLock(key, token, jedis);
            timeout -= INTERVAL_IN_MILLIS;
        }
        return got;
    }

    public static final int EXPIRED_TIME_IN_SECONDS = 60;

    private static boolean tryLock(String key, String token, Jedis jedis) {
        String got = jedis.set(key, token, "NX", "EX", EXPIRED_TIME_IN_SECONDS);
        return "OK".equals(got);
    }
}

