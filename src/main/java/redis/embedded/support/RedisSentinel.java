//package redis.embedded;
//
//import redis.embedded.RedisServer;
//
//import java.util.List;
//
//public class RedisSentinel extends RedisServer {
//    private static final String REDIS_READY_PATTERN = ".*Sentinel runid is.*";
//
//    public RedisSentinel(List<String> args, int port) {
//    }
//
//    public static RedisSentinelBuilder builder() { return new RedisSentinelBuilder(); }
//
//    @Override
//    protected String redisReadyPattern() {
//        return REDIS_READY_PATTERN;
//    }
//}
