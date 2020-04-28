package org.frameworkset.spi.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: org.frameworkset.spi.remote.http.BaseURLResponseHandler
 * @Description: TODO
 * @date 2020/4/28 15:19
 */
public abstract class BaseURLResponseHandler<T> implements URLResponseHandler<T> {
    protected String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
//	protected RuntimeException throwException1(int status, HttpEntity entity) throws IOException {
//		if (entity != null ) {
//			return new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append("\r\n").append(EntityUtils.toString(entity)).toString());
//		}
//		else{
//			return new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
//		}
//	}
}
