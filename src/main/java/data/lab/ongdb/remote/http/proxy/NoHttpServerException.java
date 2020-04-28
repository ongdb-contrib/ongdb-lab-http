package data.lab.ongdb.remote.http.proxy;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.proxy.NoHttpServerException
 * @Description: TODO
 * @date 2020/4/28 15:17
 */
public class NoHttpServerException extends RuntimeException {
    public NoHttpServerException() {
    }

    public NoHttpServerException(String message) {
        super(message);
    }

    public NoHttpServerException(Throwable cause) {
        super(cause);
    }

    public NoHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoHttpServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
