package data.lab.ongdb.remote.http.proxy.route;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.remote.http.proxy.HttpAddress;
import data.lab.ongdb.remote.http.proxy.HttpServiceHosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.route.RoutingFilter
 * @Description: TODO
 * @date 2020/4/28 15:11
 */
public class RoutingFilter {
    private static Logger logger = LoggerFactory.getLogger(RoutingFilter.class);
    private Map<String, RoutingGroup> routingGroupMap = new HashMap<String, RoutingGroup>();
    final private RoutingGroup currentRoutingGroup;
    private String currentRouting;
    private List<HttpAddress> addressList;
    private HttpServiceHosts httpServiceHosts;

    public RoutingFilter(HttpServiceHosts httpServiceHosts, List<HttpAddress> addressList, String currentRouting) {
        this.currentRouting = currentRouting;
        this.addressList = addressList;
        this.httpServiceHosts = httpServiceHosts;
        currentRoutingGroup = new RoutingGroup(httpServiceHosts);
        grouped(addressList, currentRouting);
    }

    private void grouped(List<HttpAddress> addressList, String currentRouting) {
        if (addressList == null || addressList.size() == 0)
            return;
        HttpAddress httpAddress = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Grouped http address by routing rule.currentRouting is {}", currentRouting);
        }
        List<HttpAddress> commonGroup = new ArrayList<HttpAddress>();
        for (int i = 0; i < addressList.size(); i++) {
            httpAddress = addressList.get(i);
            if (httpAddress.getRouting() == null || httpAddress.getRouting().equals("")) {
                commonGroup.add(httpAddress);
            } else if (httpAddress.getRouting().equals(currentRouting)) {
                this.currentRoutingGroup.addHttpAddress(httpAddress);
            } else {
                RoutingGroup routingGroup = routingGroupMap.get(httpAddress.getRouting());

                if (routingGroup == null) {
                    routingGroupMap.put(httpAddress.getRouting(), routingGroup = new RoutingGroup(httpServiceHosts));
                }
                routingGroup.addHttpAddress(httpAddress);

            }

        }
        this.currentRoutingGroup.after(commonGroup);
        if (logger.isDebugEnabled()) {
            logger.debug("Current RoutingGroup {} http address {}.", currentRouting, currentRoutingGroup.toString());
        }
        Iterator<Map.Entry<String, RoutingGroup>> iterator = routingGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, RoutingGroup> entry = iterator.next();
            entry.getValue().after(commonGroup);

            if (logger.isDebugEnabled()) {
                logger.debug("RoutingGroup {} http address {}.", entry.getKey(), entry.getValue().toString());
            }
        }
    }


    public HttpAddress get() {
        HttpAddress httpAddress = currentRoutingGroup.get();
        if (httpAddress == null) {
            Iterator<Map.Entry<String, RoutingGroup>> iterator = routingGroupMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, RoutingGroup> entry = iterator.next();
                httpAddress = entry.getValue().get();
                if (httpAddress != null)
                    break;
            }
        }
//		if(httpAddress == null){
//			String message = new StringBuilder().append("All Http Server ").append(addressList.toString()).append(" can't been connected.").toString();
//			throw new NoHttpServerException(message);
//		}
        return httpAddress;
    }

    public String toString() {
        if (addressList != null)
            return addressList.toString();
        return "[]";
    }

    public HttpAddress getOkOrFailed() {
        HttpAddress httpAddress = currentRoutingGroup.get();
        if (httpAddress == null) {
            Iterator<Map.Entry<String, RoutingGroup>> iterator = routingGroupMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, RoutingGroup> entry = iterator.next();
                httpAddress = entry.getValue().getOkOrFailed();
                if (httpAddress != null)
                    break;
            }
        }
        /**
         if(httpAddress == null){
         String message = new StringBuilder().append("All Http Server ").append(addressList.toString()).append(" can't been connected.").toString();
         throw new NoHttpServerException(message);
         }*/
        return httpAddress;
    }

    public static boolean access(String[] accessRoutings, HttpAddress httpHost) {
        if (accessRoutings == null || accessRoutings.length == 0)
            return true;
        String accessRouting = httpHost.getRouting();
        if (accessRouting == null) {
            return true;
        }
        for (int i = 0; i < accessRoutings.length; i++) {
            if (accessRouting.equals(accessRoutings[i])) {
                return true;
            }
        }
        return false;
    }

    public RoutingGroup getRoutingGroup(String routing) {
        return routingGroupMap.get(routing);
    }

    public int size() {
        return addressList.size();
    }
}
