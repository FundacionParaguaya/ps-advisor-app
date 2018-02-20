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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (getPort() != server.getPort()) return false;
        if (getProtocol() != null ? !getProtocol().equals(server.getProtocol()) : server.getProtocol() != null)
            return false;
        if (getHost() != null ? !getHost().equals(server.getHost()) : server.getHost() != null)
            return false;
        return getName() != null ? getName().equals(server.getName()) : server.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getProtocol() != null ? getProtocol().hashCode() : 0;
        result = 31 * result + (getHost() != null ? getHost().hashCode() : 0);
        result = 31 * result + getPort();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
