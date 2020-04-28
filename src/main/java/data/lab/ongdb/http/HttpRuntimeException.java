package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.HttpRuntimeException
 * @Description: TODO
 * @date 2020/4/28 15:21
 */
public class HttpRuntimeException extends RuntimeException {

    protected int httpStatusCode = -1;

    public HttpRuntimeException() {

    }

    public HttpRuntimeException(int httpStatusCode) {
        super();
        this.httpStatusCode = httpStatusCode;
    }

    public HttpRuntimeException(String message) {
        super(message);
    }


    public HttpRuntimeException(String message, Throwable cause, int httpStatusCode) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpRuntimeException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpRuntimeException(Throwable cause, int httpStatusCode) {
        super(cause);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpRuntimeException(Throwable cause) {
        super(cause);
    }

    public HttpRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
