package data.lab.ongdb.http.extra;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.frameworkset.util.SimpleStringUtil;
import data.lab.ongdb.http.ClientConfiguration;
import data.lab.ongdb.http.HttpRequestProxy;
import data.lab.ongdb.http.proxy.ExceptionWare;
import data.lab.ongdb.http.proxy.HttpAddress;
import data.lab.ongdb.http.proxy.HttpProxyRequestException;
import data.lab.ongdb.http.proxy.NoHttpServerException;
import data.lab.ongdb.http.util.Base64Digest;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.extra.HttpProxyRequest
 * @Description: TODO(HTTP PROXY - 持绝对接口地址 / 相对接口地址访问)
 * @date 2020/4/28 19:05
 */
public class HttpProxyRequest extends HttpRequestProxy implements HttpInter {

    private Logger logger = Logger.getLogger(this.getClass());

    private static HttpClient httpClient;

    /**
     * 指定HTTP POOL NAME
     **/
    private static String poolname;

    /**
     * 服务器授权验证
     **/
    private static String authBase64;

    public HttpProxyRequest(String poolname, String authAccount, String authPassword) {
        this.poolname = poolname;
        this.authBase64 = "Basic " + Base64Digest.encoder(authAccount + ":" + authPassword);
    }

    /**
     * @param url :支持绝对接口地址、相对接口地址、同时支持
     * @return
     * @Description: TODO(GET)
     */
    @Override
    public String httpGet(String url) {

        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

        String responseBody = null;
        String endpoint;
        Throwable e = null;
        int triesCount = 0;

        HttpGet httpGet = null;
        // 相对地址
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            endpoint = url;
            HttpAddress httpAddress = null;
            do {

                try {

                    httpAddress = config.getHttpServiceHosts().getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if (logger.isTraceEnabled()) {
                        logger.trace("sendBody call url:" + url);
                    }
                    httpClient = config.getHttpClient();

                    httpGet = new HttpGet(url);

                    responseBody = executeHttpGet(httpGet, url);

                    e = getException(null, config);
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (ConnectionPoolTimeoutException ex) {//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(config, ex);
                    break;
                } catch (ConnectTimeoutException connectTimeoutException) {
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(config, connectTimeoutException);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(config, ex);
                    break;
                } catch (NoHttpServerException ex) {
                    e = ex;

                    break;
                } catch (ClientProtocolException ex) {
                    throw new NoHttpServerException(new StringBuilder().append("Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(), ex);
                } catch (Exception ex) {
                    e = ex;
                    break;
                } catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpGet != null)
                        httpGet.releaseConnection();
                    httpClient = null;
                }


            } while (true);

            // 绝对地址
        } else {
            httpGet = new HttpGet(url);
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("sendBody call url:" + url);
                }
                httpClient = config.getHttpClient();

                responseBody = executeHttpGet(httpGet, url);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpGet != null)
                    httpGet.releaseConnection();
                httpClient = null;
            }

        }

        if (e != null) {
            if (e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException) e;
            throw new HttpProxyRequestException(e);
        }

        return responseBody;
    }

    private String executeHttpGet(HttpGet httpGet, String url) throws ClientProtocolException, IOException {
        httpGet.setHeader("User-Agent", HttpHeader.User_Agent_Firefox);

        httpGet.setHeader("Authorization", authBase64);
        httpGet.setHeader("X-Stream", "true");

        HttpResponse httpResponse = this.httpClient.execute(httpGet);

        int requestStatus = httpResponse.getStatusLine().getStatusCode();

        if (requestStatus == HttpStatus.SC_OK) {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);
            return html;
        } else {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);

            logger.info(requestStatus + "\t" + url);
            logger.error(html);

            return html;
        }
    }

    /**
     * @param url   :支持绝对接口地址、相对接口地址、同时支持
     * @param query :DSL查询
     * @return
     * @Description: TODO(POST)
     */
    @Override
    public String httpPost(String url, String query) {

        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

        String responseBody = null;
        String endpoint;
        Throwable e = null;
        int triesCount = 0;

        HttpPost httpPost = null;
        // 相对地址
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            endpoint = url;
            HttpAddress httpAddress = null;
            do {

                try {

                    httpAddress = config.getHttpServiceHosts().getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if (logger.isTraceEnabled()) {
                        logger.trace("sendBody call url:" + url);
                    }
                    httpClient = config.getHttpClient();

                    httpPost = new HttpPost(url);

                    responseBody = executeHttpPost(httpPost, query, url);
                    e = getException(null, config);
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (ConnectionPoolTimeoutException ex) {//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(config, ex);
                    break;
                } catch (ConnectTimeoutException connectTimeoutException) {
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(config, connectTimeoutException);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(config, ex);
                    break;
                } catch (NoHttpServerException ex) {
                    e = ex;

                    break;
                } catch (ClientProtocolException ex) {
                    throw new NoHttpServerException(new StringBuilder().append("Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(), ex);
                } catch (Exception ex) {
                    e = ex;
                    break;
                } catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpPost != null)
                        httpPost.releaseConnection();
                    httpClient = null;
                }


            } while (true);

            // 绝对地址
        } else {
            httpPost = new HttpPost(url);
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("sendBody call url:" + url);
                }
                httpClient = config.getHttpClient();

                responseBody = executeHttpPost(httpPost, query, url);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpPost != null)
                    httpPost.releaseConnection();
                httpClient = null;
            }

        }

        if (e != null) {
            if (e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException) e;
            throw new HttpProxyRequestException(e);
        }

        return responseBody;
    }

    private String executeHttpPost(HttpPost httpPost, String query, String url) throws ClientProtocolException, IOException {
        StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
        input.setContentType("application/json");
        httpPost.setEntity(input);

        httpPost.setHeader("Authorization", authBase64);

        HttpResponse httpResponse = this.httpClient.execute(httpPost);
        int requestStatus = httpResponse.getStatusLine().getStatusCode();

        if (requestStatus == HttpStatus.SC_OK) {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);
            return html;
        } else {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);

            logger.info(requestStatus + "\t" + url);
            logger.error(html);

            return html;
        }
    }

    /**
     * @param url   :支持绝对接口地址、相对接口地址、同时支持
     * @param query :DSL查询
     * @return
     * @Description: TODO(DELETE)
     */
    @Override
    public String postDeleteRequest(String url, String query) {
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

        String responseBody = null;
        String endpoint;
        Throwable e = null;
        int triesCount = 0;

        HttpDeleteWithBody httpDeleteWithBody = null;
        // 相对地址
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            endpoint = url;
            HttpAddress httpAddress = null;
            do {

                try {

                    httpAddress = config.getHttpServiceHosts().getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if (logger.isTraceEnabled()) {
                        logger.trace("sendBody call url:" + url);
                    }
                    httpClient = config.getHttpClient();

                    httpDeleteWithBody = new HttpDeleteWithBody(url);

                    responseBody = executeHttpDeleteWithBody(httpDeleteWithBody, query, url);
                    e = getException(null, config);
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (ConnectionPoolTimeoutException ex) {//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(config, ex);
                    break;
                } catch (ConnectTimeoutException connectTimeoutException) {
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(config, connectTimeoutException);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(config, ex);
                    break;
                } catch (NoHttpServerException ex) {
                    e = ex;

                    break;
                } catch (ClientProtocolException ex) {
                    throw new NoHttpServerException(new StringBuilder().append("Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(), ex);
                } catch (Exception ex) {
                    e = ex;
                    break;
                } catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpDeleteWithBody != null)
                        httpDeleteWithBody.releaseConnection();
                    httpClient = null;
                }


            } while (true);

            // 绝对地址
        } else {
            httpDeleteWithBody = new HttpDeleteWithBody(url);
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("sendBody call url:" + url);
                }
                httpClient = config.getHttpClient();

                responseBody = executeHttpDeleteWithBody(httpDeleteWithBody, query, url);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpDeleteWithBody != null)
                    httpDeleteWithBody.releaseConnection();
                httpClient = null;
            }

        }

        if (e != null) {
            if (e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException) e;
            throw new HttpProxyRequestException(e);
        }

        return responseBody;
    }

    private String executeHttpDeleteWithBody(HttpDeleteWithBody httpDeleteWithBody, String query, String url) throws ClientProtocolException, IOException {
        if (query != null && !query.equals("")) {
            StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
            input.setContentType("application/json");
            httpDeleteWithBody.setEntity(input);
            httpDeleteWithBody.setHeader("Authorization", authBase64);
        }
        HttpResponse httpResponse = this.httpClient.execute(httpDeleteWithBody);
        int requestStatus = httpResponse.getStatusLine().getStatusCode();
        if (requestStatus == HttpStatus.SC_OK) {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);
            return html;
        } else {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);
            logger.info(requestStatus + "\t" + url);
            logger.error(html);
            System.out.println(requestStatus + "\t" + url);
            System.out.println(html);

            return html;
        }
    }

    /**
     * @param url   :支持绝对接口地址、相对接口地址、同时支持
     * @param query :DSL查询
     * @return
     * @Description: TODO(PUT)
     */
    @Override
    public String httpPut(String url, String query) {

        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

        String responseBody = null;
        String endpoint;
        Throwable e = null;
        int triesCount = 0;

        HttpPut httpPut = null;
        // 相对地址
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            endpoint = url;
            HttpAddress httpAddress = null;
            do {

                try {

                    httpAddress = config.getHttpServiceHosts().getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if (logger.isTraceEnabled()) {
                        logger.trace("sendBody call url:" + url);
                    }
                    httpClient = config.getHttpClient();

                    httpPut = new HttpPut(url);

                    responseBody = executeHttpPut(httpPut, query, url);
                    e = getException(null, config);
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (ConnectionPoolTimeoutException ex) {//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(config, ex);
                    break;
                } catch (ConnectTimeoutException connectTimeoutException) {
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(config, connectTimeoutException);
                    if (!config.getHttpServiceHosts().reachEnd(triesCount)) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                } catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(config, ex);
                    break;
                } catch (NoHttpServerException ex) {
                    e = ex;

                    break;
                } catch (ClientProtocolException ex) {
                    throw new NoHttpServerException(new StringBuilder().append("Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(), ex);
                } catch (Exception ex) {
                    e = ex;
                    break;
                } catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpPut != null)
                        httpPut.releaseConnection();
                    httpClient = null;
                }


            } while (true);

            // 绝对地址
        } else {
            httpPut = new HttpPut(url);
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("sendBody call url:" + url);
                }
                httpClient = config.getHttpClient();

                responseBody = executeHttpPut(httpPut, query, url);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpPut != null)
                    httpPut.releaseConnection();
                httpClient = null;
            }

        }

        if (e != null) {
            if (e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException) e;
            throw new HttpProxyRequestException(e);
        }

        return responseBody;
    }

    private String executeHttpPut(HttpPut httpPut, String query, String url) throws ClientProtocolException, IOException {
        if (query != null) {
            StringEntity input = new StringEntity(query, HttpHeader.Encoding_UTF_8);
            input.setContentType("application/json");
            httpPut.setEntity(input);
            httpPut.setHeader("Authorization", authBase64);
        }

        HttpResponse httpResponse = this.httpClient.execute(httpPut);
        int requestStatus = httpResponse.getStatusLine().getStatusCode();

        if (requestStatus == HttpStatus.SC_OK) {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);
            return html;
        } else {
            byte[] temp = getResponseBody(httpResponse);
            String html = new String(temp, HttpHeader.Encoding_UTF_8);

            logger.info(requestStatus + "\t" + url);
            logger.error(html);

            return html;
        }
    }

    private static Exception getException(ResponseHandler responseHandler, ClientConfiguration configuration) {
        ExceptionWare exceptionWare = configuration.getHttpServiceHosts().getExceptionWare();
        if (exceptionWare != null) {
            return exceptionWare.getExceptionFromResponse(responseHandler);
        }
        return null;
    }

    private static HttpProxyRequestException handleConnectionPoolTimeOutException(ClientConfiguration configuration, ConnectionPoolTimeoutException ex) {
        if (configuration == null) {
            return new HttpProxyRequestException(ex);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Wait timeout for ").append(configuration.getConnectionRequestTimeout()).append("ms for idle http connection from http connection pool.");

            return new HttpProxyRequestException(builder.toString(), ex);
        }
    }

    private static HttpProxyRequestException handleConnectionTimeOutException(ClientConfiguration configuration, ConnectTimeoutException ex) {
        if (configuration == null) {
            return new HttpProxyRequestException(ex);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Build a http connection timeout for ").append(configuration.getTimeoutConnection()).append("ms.");

            return new HttpProxyRequestException(builder.toString(), ex);
        }
    }

    private static HttpProxyRequestException handleSocketTimeoutException(ClientConfiguration configuration, SocketTimeoutException ex) {
        if (configuration == null) {
            return new HttpProxyRequestException(ex);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Socket Timeout for ").append(configuration.getTimeoutSocket()).append("ms.");

            return new HttpProxyRequestException(builder.toString(), ex);
        }
    }

    /**
     * @param response :HTTP RESPONSE
     * @return
     * @Description: TODO(HTTP RESPONSE压缩格式处理)
     */
    @Override
    public byte[] getResponseBody(HttpResponse response) {
        try {
            Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");
            HttpEntity entity = response.getEntity();
            if (contentEncodingHeader != null) {
                String contentEncoding = contentEncodingHeader.getValue();
                if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
                    GZIPInputStream gzipInput = null;
                    try {
                        gzipInput = new GZIPInputStream(entity.getContent());
                    } catch (EOFException e) {
                        logger.error("read gzip inputstream eof exception!");
                    }
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[256];
                    int n;
                    while ((n = gzipInput.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                    return out.toByteArray();
                }
            }
            return EntityUtils.toByteArray(entity);
        } catch (Exception e) {
            logger.error("read response body exception! ", e);
        }

        return null;
    }
}
