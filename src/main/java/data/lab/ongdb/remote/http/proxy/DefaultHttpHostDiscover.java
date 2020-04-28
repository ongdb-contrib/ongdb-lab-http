package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.assemble.GetProperties;
import data.lab.ongdb.remote.http.ClientConfiguration;
import data.lab.ongdb.remote.http.HttpHost;

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.DefaultHttpHostDiscover
 * @Description: TODO
 * @date 2020/4/28 15:12
 */
public class DefaultHttpHostDiscover extends HttpHostDiscover {
    @Override
    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
                                      ClientConfiguration configuration,
                                      GetProperties context) {
        return null;
    }
}
