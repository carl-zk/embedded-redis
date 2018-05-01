package redis.embedded.ports;

import redis.embedded.EmbeddedRedisException;
import redis.embedded.support.PortProvider;

import java.io.IOException;
import java.net.ServerSocket;

public class EphemeralPortProvider implements PortProvider {

    public int next() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            socket.setReuseAddress(false);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            //should not ever happen
            throw new EmbeddedRedisException("Could not provide ephemeral port", e);
        }
    }
}
