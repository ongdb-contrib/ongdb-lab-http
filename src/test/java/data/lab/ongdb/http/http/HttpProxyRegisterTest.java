package data.lab.ongdb.http.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.http.extra.HttpProxyRegister;
import data.lab.ongdb.http.extra.HttpProxyRequest;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.http.HttpProxyRegisterTest
 * @Description: TODO
 * @date 2020/4/30 17:02
 */
public class HttpProxyRegisterTest {

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    private HttpProxyRequest request = new HttpProxyRequest("DEFAULT", "neo4j", "datalab%pro");

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configureAndWatch("resources" + File.separator + "log4j.properties");
        HttpProxyRegister.register("pro-ongdb-1:7474", "neo4j", "datalab%pro");
        String queryResult = request.httpGet("/user/neo4j");
        System.out.println(queryResult);
    }

    @Test
    public void name() {
        String query="{\n" +
                "    \"statements\": [\n" +
                "        {\n" +
                "            \"statement\": \"CALL dbms.cluster.overview();\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n" +
                "\n";
        String result = request.httpPost("/db/data/transaction/commit",query);
        System.out.println(result);
    }

    @Test
    public void register() {
//        String query = "{\n" +
//                "  \"statements\": [\n" +
//                "    {\n" +
//                "      \"statement\": \"MATCH p=(n)-[]-() WHERE n.name CONTAINS 'ssa' RETURN p LIMIT 10\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        String queryResult = request.httpPost("/db/data/transaction/commit", query);
//        JSONObject result = JSONObject.parseObject(queryResult);
//        System.out.println(result);
    }

}

