package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO
 * @date 2020/5/6 20:56
 */
public class DBServer {
    private String id;
    private List<Address> addressList;
    private Role role;
    private JSONArray groups;
    private String database;

    public DBServer(String id, List<Address> addressList, Role role, JSONArray groups, String database) {
        this.id = id;
        this.addressList = addressList;
        this.role = role;
        this.groups = groups;
        this.database = database;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBServer dbServer = (DBServer) o;
        return Objects.equals(id, dbServer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
