package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.client.ResponseHandler;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.ExceptionWare
 * @Description: TODO
 * @date 2020/4/28 15:12
 */
public interface ExceptionWare {
    public Exception getExceptionFromResponse(ResponseHandler responseHandler);

    void setHttpServiceHosts(HttpServiceHosts httpServiceHosts);
}
