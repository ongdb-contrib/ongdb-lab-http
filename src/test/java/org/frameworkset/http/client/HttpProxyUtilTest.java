package org.frameworkset.http.client;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.remote.http.HttpHost;
import org.frameworkset.spi.remote.http.HttpRequestProxy;
import org.frameworkset.spi.remote.http.proxy.HttpProxyUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.HttpProxyUtilTest
 * @Description: TODO
 * @date 2020/4/28 15:04
 */
public class HttpProxyUtilTest {
    @Before
    public void startPool() {
//		HttpRequestProxy.startHttpPools("application.properties");
        /**
         * 1.服务健康检查
         * 2.服务负载均衡
         * 3.服务容灾故障恢复
         * 4.服务自动发现（zk，etcd，consul，eureka，db，其他第三方注册中心）
         * 配置了两个连接池：default,report
         */
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("http.poolNames", "default,report");
//		configs.put("http.poolNames","report");
//		configs.put("report.http.health","/health");//health监控检查地址必须配置，否则将不会启动健康检查机制
//		configs.put("report.http.discoverService","org.frameworkset.http.client.DemoHttpHostDiscover");
        configs.put("http.health", "/health");//health监控检查地址必须配置，否则将不会启动健康检查机制
        configs.put("http.hosts", "192.168.137.1:808,192.168.137.1:809,192.168.137.1:810");//health监控检查地址必须配置，否则将不会启动健康检查机制
//		configs.put("http.discoverService","org.frameworkset.http.client.DemoHttpHostDiscover");


        configs.put("report.http.health", "/health");//health监控检查地址必须配置，否则将不会启动健康检查机制
        configs.put("report.http.hosts", "192.168.137.1:808,192.168.137.1:810");//health监控检查地址必须配置，否则将不会启动健康检查机制
//		configs.put("report.http.discoverService","org.frameworkset.http.client.DemoHttpHostDiscover");
        HttpRequestProxy.startHttpPools(configs);
    }

    @Test
    public void discoverTest() {

        List<HttpHost> hosts = new ArrayList<HttpHost>();
        HttpHost host = new HttpHost("192.168.137.1:808");
        hosts.add(host);

        host = new HttpHost("192.168.137.1:809");
        hosts.add(host);

        host = new HttpHost("192.168.137.1:810");
        hosts.add(host);
        HttpProxyUtil.handleDiscoverHosts("report", hosts);
    }
}
