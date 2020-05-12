package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO
 * @date 2020/5/12 12:35
 */
public class ONgDBHeartBeatTest5 {
    // HTTP
    private static final String ipPorts = "10.20.12.173:7474|10.20.13.146:7474|10.20.13.200:7474";

    // 集群
    public static void main(String[] args) {
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
        Configurator.setAllLevels("", Level.ERROR);

        // 是否打印集群路由信息
//        OngdbHeartBeat.IS_PRINT_CLUSTER_INFO = true;
        // 添加BLOT驱动
        OngdbHeartBeat.IS_ADD_BLOT_DRIVER = true;

        // 远程主机名与本地可访问的域名映射
        /**
         * 1、只有配置具体IP之后才可以使用自动路由驱动
         * 2、服务器端口如果配置了域名映射则在本地测试时必要再HOSTS文件对应配置HOSTS
         *
         * **/
        OngdbHeartBeat.setHostMap(
                "ongdb-1", "10.20.12.173",
                "ongdb-2", "10.20.13.146",
                "ongdb-replica-1", "10.20.13.200");
        OngdbHeartBeat heartBeat = new OngdbHeartBeat(ipPorts, "neo4j", "datalab%pro", 5);

        // KILL 节点持续获取连接
        for (int i = 0; i < 100000; i++) {
            System.out.println("IS REGISTER?:" + OngdbHeartBeat.isRegister());
//            System.out.println("获取READER:" + heartBeat.getReader());

            // ===================================READ NODE=====================================
            // 返回远程主机
            System.out.println("获取REMOTE READER HTTP:" + heartBeat.getReaderHttp());
            System.out.println("获取REMOTE READER BLOT:" + heartBeat.getReaderBlot());
            System.out.println("获取REMOTE READER BLOT DRIVER:" + heartBeat.getReaderBlotDriver());

            // 返回本地映射主机
            System.out.println("获取LOCALHOST ACCESS READER HTTP:" + heartBeat.getReaderHttpMappingLocal());
            System.out.println("获取LOCALHOST ACCESS READER BLOT:" + heartBeat.getReaderBlotMappingLocal());
            System.out.println("获取LOCALHOST ACCESS READER BLOT DRIVER:" + heartBeat.getReaderBlotMappingLocalDriver());

            // ===================================WRITE NODE=====================================
//            System.out.println("获取WRITER:" + heartBeat.getWriter());
            // 返回远程主机
            System.out.println("获取REMOTE WRITER HTTP:" + heartBeat.getWriterHttp());
            System.out.println("获取REMOTE WRITER BLOT:" + heartBeat.getWriterBlot());
            System.out.println("获取REMOTE WRITER BLOT DRIVER:" + heartBeat.getWriterBlotDriver());

            // 返回本地映射主机
            System.out.println("获取LOCALHOST ACCESS REMOTE WRITER HTTP:" + heartBeat.getWriterHttpMappingLocal());
            System.out.println("获取LOCALHOST ACCESS REMOTE WRITER BLOT:" + heartBeat.getWriterBlotMappingLocal());
            System.out.println("获取LOCALHOST ACCESS REMOTE WRITER BLOT DRIVER:" + heartBeat.getWriterBlotMappingLocalDriver());
        }
        // 关闭DRIVER
        OngdbHeartBeat.closeDriver();
        OngdbHeartBeat.closeDriverAsync();
    }
}

