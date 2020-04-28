package org.frameworkset.spi.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.frameworkset.spi.remote.http.HttpRuntimeException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException
 * @Description: TODO
 * @date 2020/4/28 15:17
 */
public class HttpProxyRequestException extends HttpRuntimeException {
    public HttpProxyRequestException() {
    }

    public HttpProxyRequestException(String message) {
        super(message);
    }

    public HttpProxyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpProxyRequestException(Throwable cause) {
        super(cause);
    }

    public HttpProxyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}