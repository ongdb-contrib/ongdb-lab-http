package data.lab.ongdb.http.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.common.Para
 * @Description: TODO(构造HTTP参数)
 * @date 2020/5/6 20:03
 */
public class Para {

    private static final Logger LOGGER = LogManager.getLogger(Para.class);

    private String paraName;

    private JSONObject parameters;

    public Para(String paraName, Object... paras) {
        if (paras != null) {
            if (paras.clone().length % 2 == 0) {

                this.paraName = paraName;

                this.parameters = new JSONObject();
                for (int i = 0; i < paras.length; i++) {
                    Object para = paras[i];
                    try {
                        this.parameters.put(String.valueOf(para), paras[i + 1]);
                    } catch (Exception e) {

                    }
                    i++;
                }
            } else {
                if (this.LOGGER.isDebugEnabled()) {
                    this.LOGGER.error("Statement parameters:" + paras.toString(), new IllegalArgumentException());
                }
            }
        }
    }

    public String getParaName() {
        return paraName;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "Para{" +
                "parameters=" + parameters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Para paras = (Para) o;
        return Objects.equals(parameters, paras.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}

