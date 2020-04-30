package data.lab.ongdb.http.extra;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.extra
 * @Description: TODO
 * @date 2020/4/30 17:14
 */
public class HttpRequestTest {

    private HttpRequest request = new HttpRequest("neo4j", "datalab%pro");

    // PRO
    private String server = "http://pro-ongdb-1:7474";

    // DEV
//    private String server = "http://dev-ongdb-1:7574";

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configureAndWatch("resources" + File.separator + "log4j.properties");
    }

    @Test
    public void httpGet() {
        String queryResult = request.httpGet(server+"/user/neo4j");
        System.out.println(queryResult);
    }

    @Test
    public void httpPost() {
        String query="{\n" +
                "    \"statements\": [\n" +
                "        {\n" +
                "            \"statement\": \"CALL dbms.cluster.overview();\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n" +
                "\n";
        String result = request.httpPost(server+"/db/data/transaction/commit",query);
        System.out.println(result);
    }

}

