package data.lab.ongdb.http.extra;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.extra.HttpPoolSym
 * @Description: TODO(HTTP POOL SYMBOL)
 * @date 2020/4/28 19:04
 */
public enum HttpPoolSym {

    /**
     * 多个集群时才需要新增指定HTTP连接池
     * **/

    /**
     * 默认连接池
     */
    DEFAULT("default");

    private String symbol;

    HttpPoolSym(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}
