package data.lab.ongdb.http.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.telnet
 * @Description: TODO
 * @date 2020/5/17 21:20
 */
public class TelnetUtil {

    private static final int SECOND_TO_MILL_RATE = 1000;

    /**
     * @param host
     * @param port
     * @param timeout:SECOND
     * @return
     * @Description: TODO
     */
    public static boolean telnet(String host, int port, int timeout) {
        boolean isConnected = false;
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), timeout * SECOND_TO_MILL_RATE);
            isConnected = socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isConnected;
    }
}
