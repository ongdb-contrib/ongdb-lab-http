package data.lab.ongdb.http.extra;

import org.apache.log4j.PropertyConfigurator;
import org.frameworkset.spi.async.annotation.Async;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    //    private HttpRequest request = new HttpRequest("neo4j", "datalab%pro");
    private HttpRequest request = new HttpRequest("ongdb", "datalab%pro");

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
    public void httpPost_01() {
        HttpRequest request = new HttpRequest();
        String url = "http://10.20.13.130/ongdb/graphql";
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
    public void httpPost_02() {
        HttpProxyRequest httpProxyRequest = new HttpProxyRequest();
        String url = "http://10.20.13.130/ongdb/graphql";
        String query = "{\n" +
                "    \"query\": \"query myConcernedCompany($name: String, $location: String, $isListed: Boolean, $isBond: Boolean, $tag: String, $sourceCode: String, $sourceFlag: String) {\\n  horgByName(name: $name, location: $location, tag: $tag, isListed: $isListed, isBond: $isBond, sourceCode: $sourceCode, sourceFlag: $sourceFlag) {\\n    name\\n    hcode\\n  }\\n}\\n\",\n" +
                "    \"variables\": {\n" +
                "        \"name\": \"四川和邦生物科技股份有限公司\"\n" +
                "    },\n" +
                "    \"operationName\": \"myConcernedCompany\"\n" +
                "}";
        String result = httpProxyRequest.httpPost(url, query);
        System.out.println(result);
    }

    @Test
    public void httpPost_03() {
        HttpProxyRequest httpProxyRequest = new HttpProxyRequest("DEFAULT","neo4j","123456");
        String url = "http://10.20.0.157:7474/db/data/transaction/commit";
        String query = "{\n" +
                "  \"statements\" : [ {\n" +
                "    \"statement\" : \"CREATE (n:Test01231225asd)\"\n" +
                "  }, {\n" +
                "    \"statement\" : \"CREATE (n:Test012312asd)\"\n" +
                "  } ]\n" +
                "}";
        String result = httpProxyRequest.httpPost(url, query);
        System.out.println(result);
    }

    @Test
    public void httpPost02() {
        CountDownLatch ctl = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            Async async = new Async();
            async.setCountDownLatch(ctl);
            Thread thread = new Thread(async);
            thread.start();
        }
        try {
            ctl.await(6, TimeUnit.DAYS); //等待多久为超时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public class Async implements Runnable {
        private CountDownLatch countDownLatch;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println("返回正在执行该代码的线程名称:" + Thread.currentThread().getName());
            String url = "http://10.20.13.200/db/data/transaction/commit";
            String query = "{\n" +
                    "    \"statements\": [\n" +
                    "        {\n" +
                    "            \"statement\": \"MATCH (n:HEvent)--() RETURN n LIMIT 10\",\n" +
                    "            \"resultDataContents\": [\n" +
                    "                \"row\",\n" +
                    "                \"graph\"\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            request.httpPost(url, query);
            System.out.println("ok..."+ new Random().nextInt());
            countDownLatch.countDown();
        }
    }
}

