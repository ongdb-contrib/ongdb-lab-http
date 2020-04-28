package data.lab.ongdb.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.client.ResponseHandler;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.URLResponseHandler
 * @Description: TODO
 * @date 2020/4/28 15:22
 */
public interface URLResponseHandler<T> extends ResponseHandler<T> {
    public void setUrl(String url);
}
