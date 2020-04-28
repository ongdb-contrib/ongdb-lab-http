package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.HttpConnectionKeepAliveStrategy
 * @Description: TODO
 * @date 2020/4/28 15:20
 */
public class HttpConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {
    private long keepAlive;

    public HttpConnectionKeepAliveStrategy(long keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        long keepAlive = super.getKeepAliveDuration(response, context);
        if (keepAlive == -1) {
            keepAlive = this.keepAlive;
        }
        return keepAlive;
    }

}
