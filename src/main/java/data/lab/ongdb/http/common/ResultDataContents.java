package data.lab.ongdb.http.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.common.ResultDataContents
 * @Description: TODO(定义NEO4J - HTTP接口数据返回格式)
 * @date 2020/5/6 20:01
 */
public enum ResultDataContents {

    /**
     * statement设置次参数resultDataContents
     **/

    // 按列返回数据
    ROW("row"),

    // 按图格式返回数据
    GRAPH("graph"),

    // REST_API支持的所有格式返回
    ROW_GRAPH("row,graph"),

    // 按照D3支持的绘图数据格式返回
    D3_GRAPH("d3");

    private String symbol;

    ResultDataContents(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}

