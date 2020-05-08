package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO(ACCESS PREFIX)
 * @date 2020/4/29 14:42
 */
public enum AccessPrefix {

    /**
     * BLOT单节点访问
     * **/
    SINGLE_NODE("bolt://"),

    /**
     * BLOT集群访问
     * **/
    MULTI_NODES("neo4j://"),

    /**
     * BLOT集群访问
     * **/
    MULTI_NODES_ROUTING("bolt+routing://");

    private final String symbol;

    AccessPrefix(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}

