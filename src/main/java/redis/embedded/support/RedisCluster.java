//package redis.embedded;
//
//import redis.embedded.EmbeddedRedisException;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//public class RedisCluster implements Server {
//    private final List<Server> sentinels = new LinkedList<>();
//    private final List<Server> servers = new LinkedList<>();
//
//    RedisCluster(List<Server> sentinels, List<Server> servers) {
//        this.servers.addAll(servers);
//        this.sentinels.addAll(sentinels);
//    }
//
//    @Override
//    public boolean isActive() {
//        for (Server redis : sentinels) {
//            if (!redis.isActive()) {
//                return false;
//            }
//        }
//        for (Server redis : servers) {
//            if (!redis.isActive()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void start() throws EmbeddedRedisException {
//        for (Server redis : sentinels) {
//            redis.start();
//        }
//        for (Server redis : servers) {
//            redis.start();
//        }
//    }
//
//    @Override
//    public void stop() throws EmbeddedRedisException {
//        for (Server redis : sentinels) {
//            redis.stop();
//        }
//        for (Server redis : servers) {
//            redis.stop();
//        }
//    }
//
//    public List<Server> sentinels() {
//        return sentinels;
//    }
//
//    public List<Integer> sentinelPorts() {
//        List<Integer> ports = new ArrayList<Integer>();
//        for (Server redis : sentinels) {
//        }
//        return ports;
//    }
//
//    public List<Server> servers() {
//        return servers;
//    }
//
//    public List<Integer> serverPorts() {
//        List<Integer> ports = new ArrayList<Integer>();
//        for (Server redis : servers) {
//        }
//        return ports;
//    }
//
//    public static RedisClusterBuilder builder() {
//        return new RedisClusterBuilder();
//    }
//}
