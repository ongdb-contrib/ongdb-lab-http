package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.io.File;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO
 * @date 2020/5/27 14:49
 */
public class ONgDBHeartBeatTest7 {
    // HTTP
    private static final String ipPorts = "10.20.0.157:7574";

    // HTTP
    private static final String uriBolt = "10.20.0.157:7787";

    // 集群
    public static void main(String[] args) throws InterruptedException {
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

        OngdbHeartBeat.RUN_ALL_DETECT = false;
        /**
         * 显式注册bolt驱动：在HTTP不可用时此方式可靠性较高
         * **/
        OngdbHeartBeat.explicitRegisterBolt(uriBolt);

        /**
         * @param ipPorts:'|'分隔的HOST:PORT地址
         * @param authAccount:用户名
         * @param authPassword:密码
         * @param delay:监控线程运行间隔（秒）
         * @param withMaxTransactionRetryTime:事务提交超时时间设置（秒）
         * @param heartHealthDetect:心跳检测间隔时间（秒）
         * @param timeOut:心跳检测超时时间（秒）
         * @return
         * @Description: TODO
         */
        OngdbHeartBeat heartBeat = new OngdbHeartBeat(ipPorts, "neo4j", "datalab%dev", 10, 600, 5, 10);

        /**
         * HTTP AND DRIVER本地访问服务器如下使用即可
         * **/
        // ===================================READ NODE=====================================
        // 返回本地映射主机
        System.out.println("获取LOCALHOST ACCESS READER HTTP:" + heartBeat.getReaderHttpMappingLocal());
        System.out.println("获取LOCALHOST ACCESS READER BLOT:" + heartBeat.getReaderBlotMappingLocal());
        System.out.println("获取LOCALHOST ACCESS READER BLOT DRIVER:" + heartBeat.getReaderBlotMappingLocalDriver());

        // ===================================WRITE NODE=====================================
        // 返回本地映射主机
        System.out.println("获取LOCALHOST ACCESS REMOTE WRITER HTTP:" + heartBeat.getWriterHttpMappingLocal());
        System.out.println("获取LOCALHOST ACCESS REMOTE WRITER BLOT:" + heartBeat.getWriterBlotMappingLocal());
        System.out.println("获取LOCALHOST ACCESS REMOTE WRITER BLOT DRIVER:" + heartBeat.getWriterBlotMappingLocalDriver());

        // 写数据
        Driver driver = heartBeat.getWriterBlotMappingLocalDriver();
        Session session = driver.session();
        session.run("MERGE (n:`PRE公司中文名称` {name:'test'})");

        Thread.sleep(1_000 * 60 * 60 * 24);
        // 关闭DRIVER
        OngdbHeartBeat.closeDriver();
        OngdbHeartBeat.closeDriverAsync();
    }
}
