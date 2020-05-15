package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.http.register.OngdbHeartBeat;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http
 * @Description: TODO
 * @date 2020/5/15 17:25
 */
public class ConcurentTest {
    // HTTP
    private static final String ipPorts = "10.20.0.157:7474";

    private static final String cypher = "TEST";

    private Driver driver;

    private void run(Driver driver) {
        this.driver = driver;
        for (int i = 0; i < 100; i++) {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                    this::runCypher, 0, 5, TimeUnit.SECONDS);
        }
    }

    private void runCypher() {
        try (Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run(cypher);
                // Each Cypher execution returns a stream of records.
                while (result.hasNext()) {
                    Record record = result.next();
                    // Values can be extracted from a record by index or name.
                    System.out.println("print:"+record.get("SOURCE").asString());
                }
                return null;
            });
        }
    }

    // 集群
    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
        Configurator.setAllLevels("", Level.INFO);

        // 是否打印集群路由信息
        OngdbHeartBeat.IS_PRINT_CLUSTER_INFO = true;
        // 添加BLOT驱动
        OngdbHeartBeat.IS_ADD_BLOT_DRIVER = true;

        // 远程主机名与本地可访问的域名映射
        /**
         * 1、只有配置具体IP之后才可以使用自动路由驱动
         * 2、服务器端口如果配置了域名映射则在本地测试时必要再HOSTS文件对应配置HOSTS
         * 3、自动注册BLOT驱动
         * **/
        OngdbHeartBeat.setHostMap(
                "ongdb-1", "10.20.0.157");
        OngdbHeartBeat heartBeat = new OngdbHeartBeat(ipPorts, "neo4j", "datalab%dev", 5);

        Driver driver = heartBeat.getReaderBlotMappingLocalDriver();

        new ConcurentTest().run(driver);

//        // 关闭DRIVER
//        OngdbHeartBeat.closeDriver();
//        OngdbHeartBeat.closeDriverAsync();
    }
}
