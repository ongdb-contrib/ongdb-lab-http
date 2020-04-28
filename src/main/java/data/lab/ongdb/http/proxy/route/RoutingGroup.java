package data.lab.ongdb.http.proxy.route;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.http.proxy.HttpAddress;
import data.lab.ongdb.http.proxy.HttpServiceHosts;
import data.lab.ongdb.http.proxy.RoundRobinList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.proxy.route.RoutingGroup
 * @Description: TODO
 * @date 2020/4/28 15:11
 */
public class RoutingGroup {
    protected RoundRobinList serversList;
    private HttpServiceHosts httpServiceHosts;
    protected List<HttpAddress> addressList = new ArrayList<HttpAddress>();
    private Map<String, HttpAddress> addressMap = new HashMap<String, HttpAddress>();

    public void addHttpAddress(HttpAddress httpAddress) {
        this.addressList.add(httpAddress);
        this.addressMap.put(httpAddress.getAddress(), httpAddress);

    }

    public RoutingGroup(HttpServiceHosts httpServiceHosts) {
        this.httpServiceHosts = httpServiceHosts;
    }

    public HttpServiceHosts getHttpServiceHosts() {
        return httpServiceHosts;
    }

    public HttpAddress get() {
        return serversList.getFromRouting();
    }

    public HttpAddress getOkOrFailed() {
        return serversList.getOkOrFailedFromRouting();
    }

    public void after() {
        serversList = new RoundRobinList(httpServiceHosts, this.addressList);
    }

    public void after(List<HttpAddress> commonGroup) {
        if (commonGroup != null && commonGroup.size() > 0) {
            this.addressList.addAll(commonGroup);
            for (HttpAddress httpAddress : commonGroup) {
                this.addressMap.put(httpAddress.getAddress(), httpAddress);
            }
        }
        serversList = new RoundRobinList(httpServiceHosts, this.addressList);
    }

    public String toString() {
        if (addressList != null) {
            return addressList.toString();
        }
        return "{}";
    }
}
