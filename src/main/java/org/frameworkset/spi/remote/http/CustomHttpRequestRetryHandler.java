package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.CustomHttpRequestRetryHandler
 * @Description: TODO
 * @date 2020/4/28 15:19
 */
public interface CustomHttpRequestRetryHandler {
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context, ClientConfiguration configuration);
}
