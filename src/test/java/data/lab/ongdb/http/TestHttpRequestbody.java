package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.http.HttpRequestUtil;
import org.junit.Test;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.http.client.TestHttpRequestbody
 * @Description: TODO
 * @date 2020/4/28 15:05
 */
public class TestHttpRequestbody {
    @Test
    public void testHttpJsonRequestbody() throws Exception {
        System.out.println(HttpRequestUtil.sendJsonBody("{\"id\":\"15284b36-3404-4bf8-8f14-c2114f2d97fb\",\"data\":\"国产j2ee框架 bboss\"}", "http://localhost:9096/xmlrequest/xml/echohttpjson.page"));
    }

    @Test
    public void testHttpStringRequestbody() throws Exception {
        System.out.println(HttpRequestUtil.sendStringBody("{\"id\":\"15284b36-3404-4bf8-8f14-c2114f2d97fb\",\"data\":\"国产j2ee框架 bboss\"}", "http://localhost:8080/xmlrequest/xml/echohttpstring.page"));
    }


    @Test
    public void testHttpsJsonRequestbody() throws Exception {
        System.out.println(HttpRequestUtil.sendJsonBody("{\"id\":\"15284b36-3404-4bf8-8f14-c2114f2d97fb\",\"data\":\"国产j2ee框架 bboss\"}", "https://bboss:6443/xmlrequest/xml/echohttpjson.page"));
    }

    @Test
    public void testHttpsStringRequestbody() throws Exception {
        System.out.println(HttpRequestUtil.sendStringBody("{\"id\":\"15284b36-3404-4bf8-8f14-c2114f2d97fb\",\"data\":\"国产j2ee框架 bboss\"}", "https://localhost:8443/xmlrequest/xml/echohttpstring.page"));
    }
}
