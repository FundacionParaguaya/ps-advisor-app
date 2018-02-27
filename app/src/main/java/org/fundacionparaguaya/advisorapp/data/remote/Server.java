package org.fundacionparaguaya.advisorapp.data.remote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

        Server that = (Server) o;

        return new EqualsBuilder()
                .append(protocol, that.protocol)
                .append(host, that.host)
                .append(port, that.port)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 29)
                .append(protocol)
                .append(host)
                .append(port)
                .append(name)
                .toHashCode();
    }
}
