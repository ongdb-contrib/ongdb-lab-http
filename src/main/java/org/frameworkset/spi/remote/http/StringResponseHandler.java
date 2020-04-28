package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.StringResponseHandler
 * @Description: TODO
 * @date 2020/4/28 15:22
 */
public class StringResponseHandler extends StatusResponseHandler implements URLResponseHandler<String> {

    public StringResponseHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String handleResponse(final HttpResponse response)
            throws ClientProtocolException, IOException {
        int status = initStatus(response);

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();

            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            HttpEntity entity = response.getEntity();
//             if (entity != null )
//                 throw new HttpRuntimeException(EntityUtils.toString(entity),status);
//             else
//                 throw new HttpRuntimeException("Unexpected response status: " + status,status);
            throw super.throwException(status, entity);
        }
    }

}
