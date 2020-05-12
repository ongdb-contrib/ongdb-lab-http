package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import org.neo4j.driver.Driver;

import java.util.List;
import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO
 * @date 2020/5/6 20:56
 */
public class DbServer {
    private String id;
    private List<Address> addressList;
    private Role role;
    private JSONArray groups;
    private String database;

    /**
     * false无效，true有效
     **/
    private boolean status;

    /**
     * 节点上运行的查询数量
     **/
    private int queryCount;

    /**
     * BLOT驱动器-本地访问驱动
     **/
    private Driver driverServerAddressMappingLocal;

    /**
     * BLOT驱动器-远程映射驱动
     **/
    private Driver driverServerAddress;

    /**
     * BLOT驱动器-ROUTING
     **/
    private Driver routingDriverServerAddress;

    public DbServer(String id, List<Address> addressList, Role role, JSONArray groups, String database, boolean status) {
        this.id = id;
        this.addressList = addressList;
        this.role = role;
        this.groups = groups;
        this.database = database;
        this.status = status;
    }

    public DbServer(String id, List<Address> addressList, Role role, JSONArray groups, String database, boolean status, int queryCount, Driver driverServerAddressMappingLocal, Driver driverServerAddress) {
        this.id = id;
        this.addressList = addressList;
        this.role = role;
        this.groups = groups;
        this.database = database;
        this.status = status;
        this.queryCount = queryCount;
        this.driverServerAddressMappingLocal = driverServerAddressMappingLocal;
        this.driverServerAddress = driverServerAddress;
    }

    public Driver getDriverServerAddressMappingLocal() {
        return driverServerAddressMappingLocal;
    }

    public void setDriverServerAddressMappingLocal(Driver driverServerAddressMappingLocal) {
        this.driverServerAddressMappingLocal = driverServerAddressMappingLocal;
    }

    public Driver getDriverServerAddress() {
        return driverServerAddress;
    }

    public void setDriverServerAddress(Driver driverServerAddress) {
        this.driverServerAddress = driverServerAddress;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public JSONArray getGroups() {
        return groups;
    }

    public void setGroups(JSONArray groups) {
        this.groups = groups;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Driver getRoutingDriverServerAddress() {
        return routingDriverServerAddress;
    }

    public void setRoutingDriverServerAddress(Driver routingDriverServerAddress) {
        this.routingDriverServerAddress = routingDriverServerAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DbServer dbServer = (DbServer) o;
        return Objects.equals(id, dbServer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DbServer{" +
                "id='" + id + '\'' +
                ", addressList=" + addressList +
                ", role=" + role +
                ", groups=" + groups +
                ", database='" + database + '\'' +
                ", status=" + status +
                '}';
    }
}
