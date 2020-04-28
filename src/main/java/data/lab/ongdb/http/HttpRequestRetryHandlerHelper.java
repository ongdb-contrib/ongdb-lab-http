package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.HttpRequestRetryHandlerHelper
 * @Description: TODO
 * @date 2020/4/28 15:21
 */
public class HttpRequestRetryHandlerHelper implements HttpRequestRetryHandler {
    private static Logger logger = LoggerFactory.getLogger(DefaultHttpRequestRetryHandler.class);
    private CustomHttpRequestRetryHandler httpRequestRetryHandler;
    private ClientConfiguration configuration;

    public HttpRequestRetryHandlerHelper(CustomHttpRequestRetryHandler httpRequestRetryHandler, ClientConfiguration configuration) {
        this.httpRequestRetryHandler = httpRequestRetryHandler;
        this.configuration = configuration;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount > configuration.getRetryTime()) {
            logger.warn("Maximum tries[" + configuration.getRetryTime() + "] reached for client http pool ");
            return false;
        }
        if (httpRequestRetryHandler.retryRequest(exception, executionCount, context, configuration)) {
            if (configuration.getRetryInterval() > 0) {
                try {
                    Thread.sleep(configuration.getRetryInterval());
                } catch (InterruptedException e1) {
                    return false;
                }
            }
            logger.warn(new StringBuilder().append(exception.getClass().getName()).append(" on ")
                    .append(executionCount).append(" call").toString());
            return true;
        }
        return false;
    }
}
