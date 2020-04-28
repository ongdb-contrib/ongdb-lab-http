package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.client.ResponseHandler;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.URLResponseHandler
 * @Description: TODO
 * @date 2020/4/28 15:22
 */
public interface URLResponseHandler<T> extends ResponseHandler<T> {
    public void setUrl(String url);
}
