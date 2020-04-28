package data.lab.ongdb.http.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.common.NeoUrl
 * @Description: TODO
 * @date 2020/4/28 20:06
 */
public enum NeoUrl {

    // NEO4J REST API
    DB_DATA_TRANSACTION_COMMIT("db/data/transaction/commit"),

    // LOCALHOST HTTP SERVICE API
    NEO_CSV("neo-import-csv");

    private String symbol;

    NeoUrl(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }
}
