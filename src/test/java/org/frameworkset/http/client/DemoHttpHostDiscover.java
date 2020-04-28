package org.frameworkset.http.client;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpHost;
import org.frameworkset.spi.remote.http.proxy.HttpHostDiscover;
import org.frameworkset.spi.remote.http.proxy.HttpServiceHostsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.DemoHttpHostDiscover
 * @Description: TODO
 * @date 2020/4/28 15:02
 */
public class DemoHttpHostDiscover extends HttpHostDiscover {
    private int count = 0;

    @Override
    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
                                      ClientConfiguration configuration,
                                      GetProperties context) {

        List<HttpHost> hosts = new ArrayList<HttpHost>();
        HttpHost host = new HttpHost("192.168.137.1:808|beijing");
        hosts.add(host);
        if (count != 2) {
            host = new HttpHost("192.168.137.1:809|beijing");
            hosts.add(host);
        } else {
            System.out.println("aa");
        }
        if (count > 10 && count < 15) {
            host = new HttpHost("192.168.137.1:810|beijing");
        } else {
            host = new HttpHost("192.168.137.1:810|shanghai");
            if (count == 20) { //强制移除节点
                host.setForceRemoved(true);
            }

        }

        hosts.add(host);
        count++;
        return hosts;
    }
}
