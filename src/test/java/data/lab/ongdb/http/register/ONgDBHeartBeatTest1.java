package data.lab.ongdb.http.register;

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO
 * @date 2020/5/7 9:26
 */
public class ONgDBHeartBeatTest1 {
    // HTTP
    private static final String ipPorts = "pro-ongdb-1:7474|pro-ongdb-2:7474|pro-ongdb-replica-1:7474";

    // 集群
    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
        Configurator.setAllLevels("", Level.INFO);

        // 是否打印集群路由信息
        OngdbHeartBeat.IS_PRINT_CLUSTER_INFO = true;

        // 远程主机名与本地可访问的域名映射
        OngdbHeartBeat.setHostMap(
                "ongdb-1", "pro-ongdb-1",
                "ongdb-2", "pro-ongdb-2",
                "ongdb-replica-1", "pro-ongdb-replica-1");

        OngdbHeartBeat heartBeat = new OngdbHeartBeat(ipPorts, "neo4j", "datalab%pro", 5);

        // KILL 节点持续获取连接
        for (int i = 0; i < 1000; i++) {
            System.out.println("IS REGISTER?:" + OngdbHeartBeat.isRegister());
            System.out.println("获取READER:" + heartBeat.getReader());
            // ===================================READ NODE=====================================
            // 返回远程主机
            System.out.println("获取REMOTE READER HTTP:" + heartBeat.getReaderHttp());
            System.out.println("获取REMOTE READER BLOT:" + heartBeat.getReaderBlot());
            // 返回本地映射主机
            System.out.println("获取LOCALHOST ACCESS READER HTTP:" + heartBeat.getReaderHttpMappingLocal());
            System.out.println("获取LOCALHOST ACCESS READER BLOT:" + heartBeat.getReaderBlotMappingLocal());

            // ===================================WRITE NODE=====================================
            System.out.println("获取WRITER:" + heartBeat.getWriter());
            // 返回远程主机
            System.out.println("获取REMOTE WRITER HTTP:" + heartBeat.getWriterHttp());
            System.out.println("获取REMOTE WRITER BLOT:" + heartBeat.getWriterBlot());
            // 返回本地映射主机
            System.out.println("获取LOCALHOST ACCESS REMOTE WRITER HTTP:" + heartBeat.getWriterHttpMappingLocal());
            System.out.println("获取LOCALHOST ACCESS REMOTE WRITER BLOT:" + heartBeat.getWriterBlotMappingLocal());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 关闭DRIVER
        OngdbHeartBeat.closeDriver();
        OngdbHeartBeat.closeDriverAsync();
    }
}

