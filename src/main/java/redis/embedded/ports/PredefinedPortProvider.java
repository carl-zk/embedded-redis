package redis.embedded.ports;

import redis.embedded.EmbeddedRedisException;
import redis.embedded.support.PortProvider;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PredefinedPortProvider implements PortProvider {
    private final List<Integer> ports = new LinkedList<>();
    private final Iterator<Integer> current;

    public PredefinedPortProvider(Collection<Integer> ports) {
        this.ports.addAll(ports);
        this.current = this.ports.iterator();
    }

    public synchronized int next() {
        if (!current.hasNext()) {
            throw new EmbeddedRedisException("Run out of Server ports!");
        }
        return current.next();
    }
}
