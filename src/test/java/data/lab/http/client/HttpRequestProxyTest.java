package data.lab.http.client;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.remote.http.HttpRequestProxy;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.HttpRequestProxyTest
 * @Description: TODO
 * @date 2020/4/28 15:04
 */
public class HttpRequestProxyTest {
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
        /**
         # 指定本地区信息，系统按地区部署时，指定地区信息，
         # 不同的地区请求只路由到本地区（beijing）对应的服务器，shanghai的服务器作为backup服务器，
         # 当本地(beijing)的服务器都不可用时，才将请求转发到可用的上海服务器
         */
        configs.put("http.routing", "beijing");//指定
//		configs.put("http.hosts","1111:90222,http://1111:90222,https://1111:90222");//health监控检查地址必须配置，否则将不会启动健康检查机制
        DemoHttpHostDiscover demoHttpHostDiscover = new DemoHttpHostDiscover();
        configs.put("http.discoverService", demoHttpHostDiscover);


        configs.put("report.http.health", "/health");//health监控检查地址必须配置，否则将不会启动健康检查机制
//		configs.put("report.http.hosts","1111:90222,http://1111:90222,https://1111:90222");//health监控检查地址必须配置，否则将不会启动健康检查机制
        configs.put("report.http.discoverService", "org.frameworkset.http.client.DemoHttpHostDiscover");
        /**
         # 指定本地区信息，系统按地区部署时，指定地区信息，
         # 不同的地区请求只路由到本地区（beijing）对应的服务器，shanghai的服务器作为backup服务器，
         # 当本地(beijing)的服务器都不可用时，才将请求转发到可用的上海服务器
         */
        configs.put("report.http.routing", "beijing");//指定
        HttpRequestProxy.startHttpPools(configs);
    }

    @Test
    public void testGet() {
        String data = HttpRequestProxy.httpGetforString("report", "/testBBossIndexCrud");
        System.out.println(data);
        do {
            try {
                data = HttpRequestProxy.httpGetforString("report", "/testBBossIndexCrud");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000l);
            } catch (Exception e) {
                break;
            }
            try {
                data = HttpRequestProxy.httpGetforString("/testBBossIndexCrud");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                data = HttpRequestProxy.httpGetforString("/testBBossIndexCrud");
            } catch (Exception e) {
                e.printStackTrace();
            }
//			break;
        }
        while (true);
    }
}
