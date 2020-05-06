package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.http.common.Condition;
import data.lab.ongdb.http.common.NeoUrl;
import data.lab.ongdb.http.common.ResultDataContents;
import data.lab.ongdb.http.common.Symbol;
import data.lab.ongdb.http.extra.HttpRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO(DATABASE HEARTBEAT DETECTION)
 * @date 2020/4/30 15:26
 */
public class ONgDBHeartBeat {

    private static final Logger LOGGER = LogManager.getLogger(ONgDBHeartBeat.class);

    private static final Map<Role, List<DBServer>> roleMapList = new HashMap<>();

    // 集群ROUTING
    private static final String CLUSTER_OVERVIEW_CYPHER = "CALL dbms.cluster.overview()";

    // 节点角色
    private static final String NODE_ROLE_CYPHER = "CALL dbms.cluster.role()";

    // http访问对象 支持绝对接口地址和相对接口地址
    public HttpRequest request;

    // 默认未注册心跳检测机制
    private final boolean IS_REGISTER;

    private final String[] servers;
    private final String authAccount;
    private final String authPassword;

    // 延时执行-单位秒-每隔几秒检测一次
    private final long delay;

    // 监控线程初始执行延迟设置
    private static final long INITIAL_DELAY = 1;

    public ONgDBHeartBeat(String ipPorts, String authAccount, String authPassword, int delay) {
        this.servers = Objects.requireNonNull(ipPorts).split(Symbol.SPLIT_CHARACTER.getSymbolValue());
        this.authAccount = authAccount;
        this.authPassword = authPassword;
        this.delay = delay;
        this.request = new HttpRequest(this.authAccount, this.authPassword);
        classifyNode();
        run();
        this.IS_REGISTER = true;
    }

    每隔一段时间检查一下 roleMapList 中节点的角色

    /**
     * @return
     * @Description: TODO(运行集群心跳检测机制-每隔一段事件检查一遍集群路由节点)
     */
    private void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                this::classifyNode,
                INITIAL_DELAY,
                delay,
                TimeUnit.SECONDS);
        LOGGER.info("ONgDB heartbeat detection run... " + "ongdb.heartbeat.detection.interval:" + delay + "s");
    }

    private void classifyNode() {
        for (String ipPort : servers) {
            Condition condition = new Condition();
            condition.setStatement(CLUSTER_OVERVIEW_CYPHER, ResultDataContents.ROW);
            String clusterViewStr = this.request.httpPost("http://" + ipPort + "/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());

            JSONObject object = JSONObject.parseObject(clusterViewStr);

            JSONArray dataObj = object.getJSONArray("results").getJSONObject(0).getJSONArray("data");
            dataObj.forEach(rowObj -> {
                JSONObject jsonObject = (JSONObject) rowObj;
                JSONArray jsonArray = jsonObject.getJSONArray("row");
                String id = jsonArray.getString(0);
                JSONArray addresses = jsonArray.getJSONArray(1);
                String role = jsonArray.getString(2);
                JSONArray groups = jsonArray.getJSONArray(3);
                String database = jsonArray.getString(4);
                putRoleMapList(id, addresses, role, groups, database);
            });
        }
    }

    private void putRoleMapList(String id, JSONArray addresses, String role, JSONArray groups, String database) {

        List<Address> addressList = addresses.stream().map(v -> {
            String str = (String) v;
            String[] array = str.split(":");
            String protocolStr = array[0];
            String ip = array[1].replace("//", "");
            int port = Integer.parseInt(array[2]);
            return new Address(ip, port, packProtocol(protocolStr));
        }).collect(Collectors.toList());

        Role dbRole = packRole(role);

        DBServer dbServer = new DBServer(id, addressList, dbRole, groups, database);
        if (Role.LEADER.equals(dbRole)) {
            putRoleMapList(dbServer);
        } else if (Role.FOLLOWER.equals(dbRole)) {
            putRoleMapList(dbServer);
        } else {
            putRoleMapList(dbServer);
        }
    }

    private void putRoleMapList(DBServer dbServer) {
        Role dbRole = dbServer.getRole();
        if (roleMapList.containsKey(dbRole)) {
            // 添加LIST，检查重复
            List<DBServer> dbServerList = roleMapList.get(dbRole);
            if (!dbServerList.contains(dbServer)) {
                dbServerList.add(dbServer);
            }
        } else {
            List<DBServer> dbServerList = new ArrayList<>();
            dbServerList.add(dbServer);
            roleMapList.put(dbRole, dbServerList);
        }
    }

    private Role packRole(String role) {
        if (Role.LEADER.name().equals(role)) {
            return Role.LEADER;
        } else if (Role.FOLLOWER.name().equals(role)) {
            return Role.FOLLOWER;
        } else {
            return Role.READ_REPLICA;
        }
    }

    private Protocol packProtocol(String protocolStr) {
        if (Protocol.BLOT.getValue().equals(protocolStr)) {
            return Protocol.BLOT;
        } else if (Protocol.HTTP.getValue().equals(protocolStr)) {
            return Protocol.HTTP;
        } else {
            return Protocol.HTTPS;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取注册的Reader节点 - 获取失败执行重试逻辑 ( 包括执行重新注册心跳检测机制)
     */
    public String getReader() {
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取注册的Writer节点 - 获取失败执行重试逻辑 ( 包括执行重新注册心跳检测机制)
     */
    public String getWriter() {
        return null;
    }


    /**
     * @param
     * @return
     * @Description: TODO(注册标志位)
     */
    public boolean isRegister() {
        return IS_REGISTER;
    }

}

