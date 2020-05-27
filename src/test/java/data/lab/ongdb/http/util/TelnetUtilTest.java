package data.lab.ongdb.http.util;

import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.util
 * @Description: TODO
 * @date 2020/5/17 21:22
 */
public class TelnetUtilTest {
    @Test
    public void telnet() {
        String host = "10.20.0.157";
        int port = 7574;
        /**
         * 单位秒
         * **/
        int timeout = 5;
        System.out.println(TelnetUtil.telnet(host, port, timeout));
    }
}
