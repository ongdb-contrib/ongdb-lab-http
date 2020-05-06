package data.lab.ongdb.http.register;

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO
 * @date 2020/4/30 16:07
 */
public class ONgDBHeartBeatTest {

    // HTTP
    private static final String ipPorts = "pro-ongdb-1:7474|pro-ongdb-2:7474|pro-ongdb-replica-1:7474";

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configureAndWatch("conf" + File.separator + "log4j.properties");
        Configurator.setAllLevels("", Level.INFO);
    }

    @Test
    public void run_1() {
        ONgDBHeartBeat heartBeat = new ONgDBHeartBeat(ipPorts, "neo4j", "datalab%pro", 3);
        System.out.println("IS REGISTER?:" + heartBeat.isRegister());
        System.out.println("获取负载最小的READER:" + heartBeat.getReader());
        System.out.println("获取负载最小的WRITER:" + heartBeat.getWriter());
    }
}

