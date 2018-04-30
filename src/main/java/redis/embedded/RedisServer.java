package redis.embedded;

import redis.embedded.RedisExecProvider.Server2Config;

public class RedisServer extends AbstractRedisInstance {
    private static final String REDIS_READY_PATTERN = ".*Redis is starting.*";

    public RedisServer() {
        Server2Config sc = RedisExecProvider.defaultProvider().get();
        setSc(sc);
    }


    public static RedisServerBuilder builder() {
        return new RedisServerBuilder();
    }

    @Override
    protected String redisReadyPattern() {
        return REDIS_READY_PATTERN;
    }
}
