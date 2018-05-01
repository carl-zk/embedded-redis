package redis.embedded.ports;

import redis.embedded.support.PortProvider;

import java.util.concurrent.atomic.AtomicInteger;

public class SequencePortProvider implements PortProvider {
    private AtomicInteger currentPort = new AtomicInteger(26379);

    public SequencePortProvider() {
    }

    public SequencePortProvider(int currentPort) {
        this.currentPort.set(currentPort);
    }

    public void setCurrentPort(int port) {
        currentPort.set(port);
    }

    public int next() {
        return currentPort.getAndIncrement();
    }
}
