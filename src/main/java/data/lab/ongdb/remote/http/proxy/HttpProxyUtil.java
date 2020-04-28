package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.remote.http.ClientConfiguration;
import data.lab.ongdb.remote.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.HttpProxyUtil
 * @Description: TODO
 * @date 2020/4/28 15:17
 */
public class HttpProxyUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpProxyUtil.class);

    /**
     * @param poolName
     * @param hosts
     */
    public static void handleDiscoverHosts(String poolName, List<HttpHost> hosts) {
        if (poolName == null)
            poolName = "default";
        try {
            ClientConfiguration clientConfiguration = ClientConfiguration.getClientConfiguration(poolName);
            if (clientConfiguration != null) {
                HttpHostDiscover httpHostDiscover = null;
                HttpServiceHosts httpServiceHosts = clientConfiguration.getHttpServiceHosts();
                if (httpServiceHosts != null) {
                    httpHostDiscover = httpServiceHosts.getHostDiscover();
                    if (httpHostDiscover == null) {
                        if (logger.isInfoEnabled()) {//Registry default HttpHostDiscover
                            logger.info("Registry default HttpHostDiscover to httppool[{}]", poolName);
                        }
                        synchronized (HttpProxyUtil.class) {
                            httpHostDiscover = httpServiceHosts.getHostDiscover();
                            if (httpHostDiscover == null) {
                                httpHostDiscover = new DefaultHttpHostDiscover();
                                httpHostDiscover.setHttpServiceHosts(httpServiceHosts);
                                httpServiceHosts.setHostDiscover(httpHostDiscover);
                            }
                        }
                    }
                    if (httpHostDiscover != null) {
                        if (hosts == null || hosts.size() == 0) {
                            Boolean handleNullOrEmptyHostsByDiscovery = httpHostDiscover.handleNullOrEmptyHostsByDiscovery();
                            if (handleNullOrEmptyHostsByDiscovery == null) {
                                handleNullOrEmptyHostsByDiscovery = httpServiceHosts.getHandleNullOrEmptyHostsByDiscovery();
                            }
                            if (handleNullOrEmptyHostsByDiscovery == null || !handleNullOrEmptyHostsByDiscovery) {
                                if (logger.isInfoEnabled())
                                    logger.info(new StringBuilder().append("Discovery ")
                                            .append(httpServiceHosts.getClientConfiguration().getBeanName()).append(" servers : ignore with httpHosts == null || httpHosts.size() == 0").toString());
                                return;
                            }
                        }
                        httpHostDiscover.handleDiscoverHosts(hosts);
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled())
                logger.info(new StringBuilder().append("Discovery ")
                        .append(poolName).append(" servers failed:").toString(), e);
        }


    }
//	public static HttpHostDiscover getHttpHostDiscover(String poolName){
//		ClientConfiguration clientConfiguration = ClientConfiguration.getClientConfiguration(poolName);
//		if (clientConfiguration != null){
//			HttpServiceHosts httpServiceHosts = clientConfiguration.getHttpServiceHosts();
//			return httpServiceHosts != null?httpServiceHosts.getHostDiscover():null;
//		}
//		return null;
//	}

}
