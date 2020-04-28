package data.lab.http.client;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.assemble.GetProperties;
import data.lab.ongdb.remote.http.ClientConfiguration;
import data.lab.ongdb.remote.http.HttpHost;
import data.lab.ongdb.remote.http.proxy.HttpHostDiscover;
import data.lab.ongdb.remote.http.proxy.HttpServiceHostsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.DemoHttpHostDiscover1
 * @Description: TODO
 * @date 2020/4/28 15:04
 */
public class DemoHttpHostDiscover1 extends HttpHostDiscover {
    private int count = 0;

    @Override
    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
                                      ClientConfiguration configuration,
                                      GetProperties context) {

        List<HttpHost> hosts = new ArrayList<HttpHost>();
        HttpHost host = new HttpHost("192.168.137.1:808|beijing");
        hosts.add(host);
        host = new HttpHost("192.168.137.1:809|beijing");
        hosts.add(host);
//		if(count != 2) {
//			host = new HttpHost("192.168.137.1:809|beijing");
//			hosts.add(host);
//		}
//		else{
//			System.out.println("aa");
//		}
//		if(count > 10 && count < 15) {
//			host = new HttpHost("192.168.137.1:810|beijing");
//		}
//		else
//			{
//			host = new HttpHost("192.168.137.1:810|shanghai");
//		}
        host = new HttpHost("192.168.137.1:810|shanghai");
        hosts.add(host);
        count++;
        return hosts;
    }
}
