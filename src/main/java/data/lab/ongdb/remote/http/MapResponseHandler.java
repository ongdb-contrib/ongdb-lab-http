package data.lab.ongdb.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.MapResponseHandler
 * @Description: TODO
 * @date 2020/4/28 15:21
 */
public class MapResponseHandler extends BaseResponseHandler implements URLResponseHandler<Map> {

    public MapResponseHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Map handleResponse(final HttpResponse response)
            throws ClientProtocolException, IOException {
        int status = initStatus(response);

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            if (entity.getContentLength() == 0) {
                return null;
            } else {
                return super.converJson(entity, Map.class);
            }

            //return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            HttpEntity entity = response.getEntity();
//             if (entity != null )
////            	 return SimpleStringUtil.json2Object(entity.getContent(), Map.class);
//				 throw new HttpRuntimeException(EntityUtils.toString(entity),status);
//             else
//                 throw new HttpRuntimeException("Unexpected response status: " + status,status);
            throw super.throwException(status, entity);
        }
    }


}
