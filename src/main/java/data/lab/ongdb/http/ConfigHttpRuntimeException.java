package data.lab.ongdb.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.ConfigHttpRuntimeException
 * @Description: TODO
 * @date 2020/4/28 15:19
 */
public class ConfigHttpRuntimeException extends HttpRuntimeException {


    public ConfigHttpRuntimeException() {

    }


    public ConfigHttpRuntimeException(String message, Throwable cause) {
        super(message, cause, -1);

    }

    public ConfigHttpRuntimeException(String message) {
        super(message, -1);

    }

    public ConfigHttpRuntimeException(Throwable cause) {
        super(cause, -1);
    }


}
