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
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
    }

    @Test
    public void httpGet() {
        String queryResult = request.httpGet(server + "/user/neo4j");
        System.out.println(queryResult);
    }

    @Test
    public void httpPost() {
        String query = "{\n" +
                "    \"statements\": [\n" +
                "        {\n" +
                "            \"statement\": \"CALL dbms.cluster.overview();\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n" +
                "\n";
        String result = request.httpPost(server + "/db/data/transaction/commit", query);
        System.out.println(result);
    }

    @Test
    public void httpPost01() {
        HttpRequest request = new HttpRequest();
        String url = "http://localhost/ongdb/graphql";
        String query = "{\n" +
                "    \"query\": \"query myConcernedCompany($name: String, $location: String, $isListed: Boolean, $isBond: Boolean, $tag: String, $sourceCode: String, $sourceFlag: String) {\\n  horgByName(name: $name, location: $location, tag: $tag, isListed: $isListed, isBond: $isBond, sourceCode: $sourceCode, sourceFlag: $sourceFlag) {\\n    name\\n    hcode\\n  }\\n}\\n\",\n" +
                "    \"variables\": {\n" +
                "        \"name\": \"四川和邦生物科技股份有限公司\"\n" +
                "    },\n" +
                "    \"operationName\": \"myConcernedCompany\"\n" +
                "}";
        String result = request.httpPost(url, query);
        System.out.println(result);
    }

    @Test
    public void httpPost02() {
        HttpRequest request = new HttpRequest("ongdb","datalab%pro");
        String url = "http://localhost/db/data/transaction/commit";
        String query = "{\n" +
                "    \"statements\": [\n" +
                "        {\n" +
                "            \"statement\": \"MATCH (n:HEvent)--() RETURN n LIMIT 1000000\",\n" +
                "            \"resultDataContents\": [\n" +
                "                \"row\",\n" +
                "                \"graph\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String result = request.httpPost(url, query);
        System.out.println(result);
    }
}

