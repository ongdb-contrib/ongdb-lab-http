package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO(ADDRESS)
 * @date 2020/4/29 14:43
 */
public class Address {
    /**
     * 发现的HOST
     **/
    private String host;
    private int port;
    private Protocol protocol;
    /**
     * 初始化传入的HOST
     **/
    private String initHost;

    /**
     * false无效，true有效
     **/
    private boolean status;

    /**
     * @param host:远程主机
     * @param port:端口
     * @param initHost:初始化主机
     * @return
     * @Description: TODO
     */
    public Address(String host, int port, String initHost) {
        this.host = host;
        this.port = port;
        this.initHost = initHost;
    }

    /**
     * @param host:远程主机
     * @param port:端口
     * @param protocol:协议类型
     * @param status:地址可用状态
     * @param initHost:初始化主机
     * @return
     * @Description: TODO
     */
    public Address(String host, int port, Protocol protocol, String initHost, boolean status) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.initHost = initHost;
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getInitHost() {
        return initHost;
    }

    public void setInitHost(String initHost) {
        this.initHost = initHost;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getServerAddress() {
        return this.host + ":" + port;
    }

    public String getServerAddressMappingLocal() {
        return this.initHost + ":" + port;
    }
}

