package data.lab.ongdb.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.assemble.GetProperties;
import data.lab.ongdb.http.ClientConfiguration;
import data.lab.ongdb.http.HttpHost;

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.proxy.DefaultHttpHostDiscover
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
