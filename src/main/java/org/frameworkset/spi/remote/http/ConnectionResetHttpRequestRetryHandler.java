package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.ConnectionResetHttpRequestRetryHandler
 * @Description: TODO
 * @date 2020/4/28 15:19
 */
public class ConnectionResetHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {


    /**
     * Determines if a method should be retried after an IOException
     * occurs during execution.
     *
     * @param exception the exception that occurred
     * @return {@code true} if the method should be retried, {@code false}
     * otherwise
     */
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context, ClientConfiguration configuration) {
        if (super.retryRequest(exception, executionCount, context, configuration)) {
            return true;
        } else if (exception instanceof SocketException) {
            String message = exception.getMessage();
            if (message != null && message.trim().equals("Connection reset")) {
                return true;
            }
        }
        return false;
    }
}
