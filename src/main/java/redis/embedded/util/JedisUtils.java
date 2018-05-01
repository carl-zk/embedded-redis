//package redis.embedded.util;
//
//import redis.embedded.Server;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class JedisUtils {
//    public static Set<String> jedisHosts(Server redis) {
//        return portsToJedisHosts(null);
//    }
//
//    public static Set<String> sentinelHosts(RedisCluster cluster) {
//        final List<Integer> ports = cluster.sentinelPorts();
//        return portsToJedisHosts(ports);
//    }
//
//    public static Set<String> portsToJedisHosts(List<Integer> ports) {
//        Set<String> hosts = new HashSet<String>();
//        for (Integer p : ports) {
//            hosts.add("localhost:" + p);
//        }
//        return hosts;
//    }
//}
