package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.register
 * @Description: TODO(协议类型)
 * @date 2020/5/6 21:00
 */
public enum Protocol {

    BLOT("bolt"),
    HTTP("http"),
    HTTPS("https");

    private String value;

    Protocol(String symbol) {
        this.value = symbol;
    }

    public String getValue() {
        return this.value;
    }

}
