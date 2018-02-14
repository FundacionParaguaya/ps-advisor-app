package org.fundacionparaguaya.advisorapp.data.remote;

/**
 * A remote server API that can be connected to.
 */
public class Server {
    private String protocol;
    private String host;
    private int port;
    private String name;

    Server(String protocol, String host, int port, String name) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
