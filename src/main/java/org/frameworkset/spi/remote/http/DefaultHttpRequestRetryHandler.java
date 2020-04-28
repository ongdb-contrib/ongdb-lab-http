package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.DefaultHttpRequestRetryHandler
 * @Description: TODO
 * @date 2020/4/28 15:20
 */
public class DefaultHttpRequestRetryHandler implements CustomHttpRequestRetryHandler {


    /**
     * Determines if a method should be retried after an IOException
     * occurs during execution.
     *
     * @param exception the exception that occurred
     * @return {@code true} if the method should be retried, {@code false}
     * otherwise
     */
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context, ClientConfiguration configuration) {
        if (exception instanceof HttpHostConnectException     //NoHttpResponseException 重试
                || exception instanceof ConnectTimeoutException //连接超时重试
                || exception instanceof UnknownHostException
                || exception instanceof NoHttpResponseException
//              || exception instanceof SocketTimeoutException    //响应超时不重试，避免造成业务数据不一致
        ) {

            return true;
        }

        return false;
    }
}
