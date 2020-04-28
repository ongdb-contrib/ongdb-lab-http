package data.lab.ongdb.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.HttpDeleteWithBody
 * @Description: TODO
 * @date 2020/4/28 15:20
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "DELETE";

    public HttpDeleteWithBody() {
        super();
    }

    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
