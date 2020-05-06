package data.lab.ongdb.http.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Yc-Ma 
 * @PACKAGE_NAME: data.lab.ongdb.etl.model
 * @Description: TODO(NEO4J - HTTP请求体查询参数)
 * @date 2019/7/10 10:00
 */
public class Condition {

    private static final Logger LOGGER = LogManager.getLogger(Condition.class);

    private JSONArray statements = new JSONArray();

    public Condition() {
    }

    public Condition(String statements) {
        this.setStatement(statements,ResultDataContents.ROW_GRAPH);
    }

    public void setStatement(String statement, ResultDataContents contents, String paraName, Object... paras) {
        JSONObject parameters = new JSONObject();
        parameters.put("statement", statement);
        parameters.put("resultDataContents", packContentsCondition(contents));

        if (paras != null && paraName != null) {
            JSONObject props = new JSONObject();

            if (paras.clone().length % 2 == 0) {

                JSONObject paraMap = new JSONObject();
                for (int i = 0; i < paras.length; i++) {
                    Object para = paras[i];
                    try {
                        paraMap.put(String.valueOf(para), paras[i + 1]);
                    } catch (Exception e) {

                    }
                    i++;
                }
                props.put(paraName, paraMap);
                parameters.put("parameters", props);
                statements.add(parameters);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error("Statement parameters:" + paras.toString(), new IllegalArgumentException());
                }
            }
        }
    }

    private JSONArray packContentsCondition(ResultDataContents contents) {
        contents = resetContens(contents);
        JSONArray array = new JSONArray();
        if (contents.equals(ResultDataContents.ROW_GRAPH)) {
            String[] split = contents.getSymbolValue().split(Symbol.COMMA_CHARACTER.getSymbolValue());
            for (int i = 0; i < split.length; i++) {
                String symbol = split[i];
                array.add(symbol);
            }
        } else {
            array.add(contents.getSymbolValue());
        }
        return array;
    }

    private ResultDataContents resetContens(ResultDataContents contents) {
        if (ResultDataContents.D3_GRAPH.equals(contents)) {
            return ResultDataContents.GRAPH;
        }
        return contents;
    }

    public void setStatement(String statement, ResultDataContents contents, Para para) {
        JSONObject parameters = new JSONObject();
        parameters.put("statement", statement);
        parameters.put("resultDataContents", packContentsCondition(contents));
        JSONObject paraMap = new JSONObject();
        paraMap.put(para.getParaName(), para.getParameters());
        parameters.put("parameters", paraMap);
        statements.add(parameters);
    }

    public void setStatement(String statement, ResultDataContents contents, Para... paras) {
        JSONObject parameters = new JSONObject();
        parameters.put("statement", statement);
        parameters.put("resultDataContents", packContentsCondition(contents));
        Para[] paraArray = paras.clone();
        JSONObject paraMap = new JSONObject();
        for (int i = 0; i < paraArray.length; i++) {
            Para para = paraArray[i];
            paraMap.put(para.getParaName(), para.getParameters());
        }
        parameters.put("parameters", paraMap);
        statements.add(parameters);
    }

    public void setStatement(String statement, ResultDataContents contents) {
        JSONObject parameters = new JSONObject();
        parameters.put("statement", statement);
        parameters.put("resultDataContents", packContentsCondition(contents));
        statements.add(parameters);
    }

    public void setStatement(String statement, ResultDataContents contents, List<Para> paraList) {
        JSONObject parameters = new JSONObject();
        parameters.put("statement", statement);
        parameters.put("resultDataContents", packContentsCondition(contents));
        JSONObject paraMap = new JSONObject();
        for (int i = 0; i < paraList.size(); i++) {
            Para para = paraList.get(i);
            paraMap.put(para.getParaName(), para.getParameters());
        }
        parameters.put("parameters", paraMap);
        statements.add(parameters);
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put("statements", statements);
        return object.toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(从参数里面解析cypher)
     */
    public String getStatement(String toString) {
        return JSONObject.parseObject(toString)
                .getJSONArray("statements")
                .getJSONObject(0)
                .getString("statement");
    }

}


