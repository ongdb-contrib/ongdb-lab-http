package data.lab.ongdb.http.http.server;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.http.http.server.HttpServiceTest
 * @Description: TODO
 * @date 2020/4/30 17:01
 */
public class HttpServiceTest {

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");
    }

    @Test
    public void test01() {
//        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");
//        String ipPorts = "192.168.12.19:7687";
//        NeoComposer composer = new NeoComposer(ipPorts, "neo4j", "123456");
//        String[] data = new String[]{"asdsad32423,john\r\n", "345ssadsadsa,peter\r\n", "dsadsad,刘备\r\n", "sadsadasda,诸葛亮\r\n",
//                "sadsadasdasd3v,司马懿\r\n"};
//        for (int i = 0; i < data.length; i++) {
//            String datum = data[i];
//            composer.writeCsvBody("node.csv", datum);
//        }
    }

//    @Test
//    public void test02() throws Exception {

//        NeoComposer neoComposer = new NeoComposer("localhost:7687", "neo4j", "123456");
//        neoComposer.setDEBUG(true);
//
//        String[] eids = new String[]{"681", "682", "683"};
//        for (String eid : eids) {
//            JSONObject result = getResult(eid);
//            JSONArray nodes = JSONTool.getNodeOrRelaList(result, "nodes");
//            JSONArray relationships = JSONTool.getNodeOrRelaList(result, "relationships");
//
//
//            // 生成节点批量导入CYPHER
//            List<UpdateNode> nodeList = nodes.stream().map(nodeObj -> {
//                JSONObject nodeObject = (JSONObject) nodeObj;
//                UpdateNode updateNode = new UpdateNode();
//                JSONObject properties = nodeObject.getJSONObject("properties");
//
//                properties.put("name", properties.getString("_unique_uuid"));
//                properties.put("nameNodeSpace", properties.getString("_entity_name"));
//                properties.remove("id");
//                properties.remove("type");
//                properties.remove("keywords");
//
//                updateNode.set_unique_uuid(properties.getString("_unique_uuid"));
//                updateNode.set_entity_name(properties.getString("_entity_name"));
//                updateNode.setProperties(properties);
//                JSONArray labels = nodeObject.getJSONArray("labels");
//                String label = labels.getString(0);
//                if ("组织".equals(label)) {
//                    updateNode.setLabel(NowLabel.组织);
//                } else if ("账号".equals(label)) {
//                    updateNode.setLabel(NowLabel.TwitterID);
//                } else {
//                    updateNode.setLabel(NowLabel.事件);
//                }
//                return updateNode;
//            }).collect(Collectors.toCollection(ArrayList::new));
//            neoComposer.addMergeDynamicNodes(nodeList);
//
//            // 生成关系批量导入CYPHER
//            List<UpdateRela> updateRelas = relationships.stream().map(rela -> {
//                JSONObject relaObj = (JSONObject) rela;
//                JSONObject properties = relaObj.getJSONObject("properties");
//
//                properties.put("name", relaObj.getString("type"));
//                properties.remove("id");
//
//                UpdateRela updateRela = new UpdateRela();
//                updateRela.setFrom(getLabel(relaObj.getLongValue("startNode"), nodes), getStr(relaObj.getLongValue("startNode"), nodes));
//                updateRela.setTo(getLabel(relaObj.getLongValue("endNode"), nodes), getStr(relaObj.getLongValue("endNode"), nodes));
//                updateRela.set_entity_name(properties.getString("_entity_name"));
//                if ("参与".equals(relaObj.getString("type"))) {
//                    updateRela.setType(NowRelationType.参与);
//                } else {
//                    updateRela.setType(NowRelationType.参与);
//                }
//                updateRela.setProperties(properties);
//                return updateRela;
//            }).collect(Collectors.toCollection(ArrayList::new));
//
//            neoComposer.addMergeDynamicRelations(updateRelas);
//
//            System.out.println(neoComposer.addMergeDynamicNodes);
//            System.out.println(neoComposer.addMergeDynamicRelations);
//
//            neoComposer.execute();
//            neoComposer.reset();
//        }
//    }

//    private String getStr(long startNode, JSONArray nodes) {
//        for (Object object : nodes) {
//            JSONObject obj = (JSONObject) object;
//            long id = obj.getLongValue("id");
//            if (id == startNode) {
//                JSONObject properties = obj.getJSONObject("properties");
//                return properties.getString("_unique_uuid");
//            }
//        }
//        return null;
//    }

//    private Label getLabel(long startNode, JSONArray nodes) {
//        for (Object object : nodes) {
//            JSONObject obj = (JSONObject) object;
//            long id = obj.getLongValue("id");
//            if (id == startNode) {
//                JSONArray labels = obj.getJSONArray("labels");
//                String label = labels.getString(0);
//                if ("组织".equals(label)) {
//                    return NowLabel.组织;
//                } else if ("账号".equals(label)) {
//                    return NowLabel.TwitterID;
//                } else {
//                    return NowLabel.事件;
//                }
//            }
//        }
//        return Label.label(null);
//    }

//    private JSONObject getResult(String eid) throws Exception {
//        String url = "http://6012.1021.6521.21912:8108/subsys/json_graph_graphInit.action";
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//
//        HttpPost uploadFile = new HttpPost(url);
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addTextBody("eid", eid, ContentType.TEXT_PLAIN); // 624 501
//        HttpEntity multipart = builder.build();
//        uploadFile.setEntity(multipart);
//        CloseableHttpResponse response = httpClient.execute(uploadFile);
//        HttpEntity responseEntity = response.getEntity();
//        String sResponse = EntityUtils.toString(responseEntity, "UTF-8");
//        System.out.println("Post Result:" + sResponse);
//
//        return JSONObject.parseObject(sResponse);
//    }

}




