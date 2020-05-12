package data.lab.ongdb.http.common;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.common.Symbol
 * @Description: TODO(枚举类型, 符号)
 * @date 2019/7/9 9:26
 */
public enum Symbol {

    /**
     * 数据分割符
     **/
    SPECIAL_SPLIT("-SPLIT-&-"),

    /**
     * 下划线
     **/
    DIVIDE_SPLIT("/"),

    /**
     * 逗号
     **/
    COMMA_CHARACTER(","),

    /**
     * 分割多配置项
     **/
    SPLIT_CHARACTER("\\|"),

    /**
     * 英文冒号
     **/
    COLON(":"),

    /**
     * 英文句号
     **/
    POINT("\\.");

    private String symbol;

    Symbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}
