package data.lab.ongdb.http.register;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.http.common.Condition;
import data.lab.ongdb.http.common.NeoUrl;
import data.lab.ongdb.http.common.ResultDataContents;
import data.lab.ongdb.http.common.Symbol;
import data.lab.ongdb.http.extra.HttpPoolSym;
import data.lab.ongdb.http.extra.HttpProxyRegister;
import data.lab.ongdb.http.extra.HttpProxyRequest;
import data.lab.ongdb.http.extra.HttpRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.etl.register
 * @Description: TODO(DATABASE HEARTBEAT DETECTION)
 * @date 2020/4/30 15:26
 */
public class OngdbHeartBeat {

    private static final Logger LOGGER = LogManager.getLogger(OngdbHeartBeat.class);

    private static final Map<String, String> HOST_MAP = new HashMap<>();

    private static final Map<Role, CopyOnWriteArrayList<DbServer>> ROLE_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 集群ROUTING
     **/
    private static final String CLUSTER_OVERVIEW_CYPHER = "CALL dbms.cluster.overview()";

    /**
     * 节点角色
     **/
    private static final String NODE_ROLE_CYPHER = "CALL dbms.cluster.role()";

    /**
     * 获取注册单节点时的配置
     **/
    private static final String SINGLE_NODE_CONF = "CALL dbms.listConfig() YIELD name,description,value WHERE name=~'.*dbms.connector.http.*listen_address.*|.*dbms.connector.bolt.listen_address*' RETURN name,description,value";

    /**
     * 统计节点上运行的查询数量
     **/
    private static final String QUERY_COUNT = "CALL dbms.listQueries() YIELD queryId RETURN count(*) AS count";

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    public HttpProxyRequest request;
    public HttpRequest originalRequest;

    /**
     * 默认未注册心跳检测机制
     **/
    private final boolean IS_REGISTER;

    private final String[] servers;

    /**
     * 延时执行-单位秒-每隔几秒检测一次
     * ROLE MAP分类运行延时参数
     * 健康检查以及节点角色检查延时参数
     **/
    private final long delay;

    /**
     * 监控线程初始执行延迟设置
     **/
    private static final long INITIAL_DELAY = 1;

    /**
     * 是否打印集群路由信息
     **/
    public static boolean IS_PRINT_CLUSTER_INFO = false;

    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword, int delay) {
        this.servers = Objects.requireNonNull(ipPorts).split(Symbol.SPLIT_CHARACTER.getSymbolValue());
        this.delay = delay;

        HttpProxyRegister.register(getIpPortsStr(ipPorts), authAccount, authPassword);
        this.request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue(), authAccount, authPassword);
        this.originalRequest = new HttpRequest(authAccount, authPassword);

        // 多节点运行监控线程
        if (HOST_MAP.size() > 1) {
            // 初始化运行
            initRun();

            // 线程池运行
            threadPoolRun();
        } else {
            // 单节点不运行监控线程
            packDefaultHost();
        }
        this.IS_REGISTER = true;
    }

    private void initRun() {
        // 执行一次节点列表ROLE MAP分类
        classifyNode();

        // 节点角色监控
        validCheck();

        // 检查节点负载情况
        checkTheLoad();

        // 移除无效状态的DB SERVER
        removeNotValid();
    }

    /**
     * @return
     * @Description: TODO(运行集群心跳检测机制 - 每隔一段事件检查一遍集群路由节点)(监控集群节点列表的变化 - 并添加到ROLE MAP LIST中)
     */
    private void threadPoolRun() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::classifyNode, INITIAL_DELAY, this.delay, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::validCheck, INITIAL_DELAY, this.delay, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::checkTheLoad, INITIAL_DELAY, this.delay, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::removeNotValid, INITIAL_DELAY, this.delay, TimeUnit.SECONDS);
        LOGGER.info("ONgDB heartbeat detection run... " + "ongdb.heartbeat.detection.interval:" + this.delay + "s");
    }

    private void packDefaultHost() {
        CopyOnWriteArrayList<DbServer> dbServerList = new CopyOnWriteArrayList<>();
        List<Address> addressList = getSingleNodeInfo();
        if (!addressList.isEmpty()) {
            String id = "ongdb-single-node";
            Role role = Role.LEADER;
            JSONArray groups = new JSONArray();
            String database = "localhost";
            DbServer dbServer = new DbServer(id, addressList, role, groups, database, true);
            dbServerList.add(dbServer);
            ROLE_LIST_MAP.put(Role.LEADER, dbServerList);
        }
    }

    private List<Address> getSingleNodeInfo() {
        List<Address> addressList = new ArrayList<>();
        Condition condition = new Condition();
        condition.setStatement(SINGLE_NODE_CONF, ResultDataContents.ROW);
        try {
            String singleNodeConf = this.request.httpPost("/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());
            JSONObject object = JSONObject.parseObject(singleNodeConf);
            JSONArray dataObj = object.getJSONArray("results").getJSONObject(0).getJSONArray("data");

            String host = "localhost";
            String initHost = "localhost";
            for (Map.Entry entry : HOST_MAP.entrySet()) {
                host = String.valueOf(entry.getKey());
                initHost = String.valueOf(entry.getValue());
            }
            boolean status = true;

            for (Object rowObj : dataObj) {
                JSONObject jsonObject = (JSONObject) rowObj;
                JSONArray jsonArray = jsonObject.getJSONArray("row");
                String name = jsonArray.getString(0);
                String value = jsonArray.getString(2);
                int port = Integer.parseInt(value.split(":")[1]);

                if ("dbms.connector.bolt.listen_address".equals(name)) {
                    addressList.add(new Address(host, port, Protocol.BLOT, initHost, status));
                } else if ("dbms.connector.http.listen_address".equals(name)) {
                    addressList.add(new Address(host, port, Protocol.HTTP, initHost, status));
                } else {
                    // dbms.connector.https.listen_address
                    addressList.add(new Address(host, port, Protocol.HTTPS, initHost, status));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Single ongdb node conf error!");
        }
        return addressList;
    }

    private String getIpPortsStr(String ipPorts) {
        return ipPorts.replace("|", ",");
    }

    /**
     * @param
     * @return
     * @Description: TODO(KEY是远程节点的主机名 ， VALUE是可以访问远程节点的主机名或者IP)
     */
    public static void setHostMap(String... kv) {
        if (kv.length == 1) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < kv.length; i++) {
                if (i == kv.length - 1) {
                    break;
                }
                String key = kv[i];
                HOST_MAP.put(key, kv[i + 1]);
                i += 1;
            }
        }
    }

    private void checkTheLoad() {
        if (IS_PRINT_CLUSTER_INFO) {
            LOGGER.info("Load check...");
        }
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                // HTTP地址
                Address httpAddress = getHttpAddress(server);
                String ipPort = getHost(httpAddress.getHost()) + ":" + httpAddress.getPort();

                Condition condition = new Condition();
                condition.setStatement(QUERY_COUNT, ResultDataContents.ROW);
                try {
                    String queryCountStr = this.originalRequest.httpPost("http://" + ipPort + "/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());
                    JSONObject object = JSONObject.parseObject(queryCountStr);
                    JSONArray dataObj = object.getJSONArray("results").getJSONObject(0).getJSONArray("data");
                    for (Object rowObj : dataObj) {
                        JSONObject jsonObject = (JSONObject) rowObj;
                        JSONArray jsonArray = jsonObject.getJSONArray("row");
                        int count = jsonArray.getIntValue(0);

                        // 将QUERY COUNT参数更新到对应的DbServer中
                        server.setQueryCount(count);
                    }
                } catch (Exception e) {
                    LOGGER.error("Check load fail:" + server.toString() + e);
                }
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(移除无效状态的DB SERVER)
     */
    private void removeNotValid() {
        if (IS_PRINT_CLUSTER_INFO) {
            LOGGER.info("Remove not valid db server...");
        }
        CopyOnWriteArrayList<DbServer> serverLeaderList = ROLE_LIST_MAP.containsKey(Role.LEADER) ? ROLE_LIST_MAP.get(Role.LEADER)
                .stream()
                .filter(DbServer::isStatus).collect(Collectors.toCollection(CopyOnWriteArrayList::new)) : new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<DbServer> serverFollowerList = ROLE_LIST_MAP.containsKey(Role.FOLLOWER) ? ROLE_LIST_MAP.get(Role.FOLLOWER)
                .stream()
                .filter(DbServer::isStatus).collect(Collectors.toCollection(CopyOnWriteArrayList::new)) : new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<DbServer> serverReadReplicaList = ROLE_LIST_MAP.containsKey(Role.READ_REPLICA) ? ROLE_LIST_MAP.get(Role.READ_REPLICA)
                .stream()
                .filter(DbServer::isStatus).collect(Collectors.toCollection(CopyOnWriteArrayList::new)) : new CopyOnWriteArrayList<>();
        if (!serverLeaderList.isEmpty()) {
            ROLE_LIST_MAP.put(Role.LEADER, serverLeaderList);
        } else {
            ROLE_LIST_MAP.remove(Role.LEADER);
        }
        if (!serverFollowerList.isEmpty()) {
            ROLE_LIST_MAP.put(Role.FOLLOWER, serverFollowerList);
        } else {
            ROLE_LIST_MAP.remove(Role.FOLLOWER);
        }
        if (!serverReadReplicaList.isEmpty()) {
            ROLE_LIST_MAP.put(Role.READ_REPLICA, serverReadReplicaList);
        } else {
            ROLE_LIST_MAP.remove(Role.READ_REPLICA);
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点角色监控)
     */
    private void validCheck() {
        if (IS_PRINT_CLUSTER_INFO) {
            LOGGER.info("Valid check...");
        }
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {

                Role role = server.getRole();
                // HTTP地址
                Address httpAddress = getHttpAddress(server);
                String ipPort = getHost(httpAddress.getHost()) + ":" + httpAddress.getPort();

                Condition condition = new Condition();
                condition.setStatement(NODE_ROLE_CYPHER, ResultDataContents.ROW);
                try {
                    String clusterViewStr = this.originalRequest.httpPost("http://" + ipPort + "/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());

                    Role currentRole = getRoleFromResult(clusterViewStr);
                    if (!role.equals(currentRole)) {
                        server.setStatus(false);
                    }
                } catch (Exception e) {
                    LOGGER.error("Valid check fail:" + server.toString() + e);
                }
            }
        }
    }

    private String getHost(String remoteHost) {
        if (HOST_MAP.containsKey(remoteHost)) {
            return HOST_MAP.get(remoteHost);
        }
        return remoteHost;
    }

    private Role getRoleFromResult(String clusterViewStr) {
        JSONObject object = JSONObject.parseObject(clusterViewStr);
        JSONArray dataObj = object.getJSONArray("results").getJSONObject(0).getJSONArray("data");
        for (Object rowObj : dataObj) {
            JSONObject jsonObject = (JSONObject) rowObj;
            JSONArray jsonArray = jsonObject.getJSONArray("row");
            String roleStr = jsonArray.getString(0);
            return packRole(roleStr);
        }
        return Role.OTHER;
    }

    private Address getHttpAddress(DbServer server) {
        List<Address> addressList = server.getAddressList();
        for (Address address : addressList) {
            if (Protocol.HTTP.equals(address.getProtocol())) {
                return address;
            }
        }
        return new Address("127.0.0.1", 7474, Protocol.HTTP, "localhost", true);
    }

    /**
     * @param
     * @return
     * @Description: TODO(监控集群节点列表的变化 - 并添加到ROLE MAP LIST中)
     */
    private void classifyNode() {
        for (String ipPort : this.servers) {
            Condition condition = new Condition();
            condition.setStatement(CLUSTER_OVERVIEW_CYPHER, ResultDataContents.ROW);
            try {
                String clusterViewStr = this.request.httpPost("/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());

                JSONObject object = JSONObject.parseObject(clusterViewStr);

                JSONArray dataObj = object.getJSONArray("results").getJSONObject(0).getJSONArray("data");
                for (Object rowObj : dataObj) {
                    JSONObject jsonObject = (JSONObject) rowObj;
                    JSONArray jsonArray = jsonObject.getJSONArray("row");
                    String id = jsonArray.getString(0);
                    JSONArray addresses = jsonArray.getJSONArray(1);
                    String role = jsonArray.getString(2);
                    JSONArray groups = jsonArray.getJSONArray(3);
                    String database = jsonArray.getString(4);
                    putRoleMapList(id, addresses, role, groups, database);
                }
            } catch (Exception e) {
                LOGGER.error("Node connect refused:" + ipPort);
            }
        }
        if (IS_PRINT_CLUSTER_INFO) {
            printClusterRoutingInfo();
        }
    }

    private void printClusterRoutingInfo() {
        JSONObject object = JSONObject.parseObject(JSON.toJSONString(ROLE_LIST_MAP));
        LOGGER.info("Ongdb cluster routing information:" + object.toJSONString());
    }

    private void putRoleMapList(String id, JSONArray addresses, String role, JSONArray groups, String database) {

        List<Address> addressList = addresses.stream().map(v -> {
            String str = (String) v;
            String[] array = str.split(":");
            String protocolStr = array[0];
            String ip = array[1].replace("//", "");
            int port = Integer.parseInt(array[2]);
            return new Address(ip, port, packProtocol(protocolStr), getHost(ip), true);
        }).collect(Collectors.toList());

        Role dbRole = packRole(role);

        DbServer dbServer = new DbServer(id, addressList, dbRole, groups, database, true);
        if (Role.LEADER.equals(dbRole)) {
            putRoleMapList(dbServer);
        } else if (Role.FOLLOWER.equals(dbRole)) {
            putRoleMapList(dbServer);
        } else {
            putRoleMapList(dbServer);
        }
    }

    private void putRoleMapList(DbServer dbServer) {
        Role dbRole = dbServer.getRole();
        if (ROLE_LIST_MAP.containsKey(dbRole)) {
            // 添加LIST，检查重复
            List<DbServer> dbServerList = ROLE_LIST_MAP.get(dbRole);
            if (!dbServerList.contains(dbServer)) {
                dbServerList.add(dbServer);
            }
        } else {
            CopyOnWriteArrayList<DbServer> dbServerList = new CopyOnWriteArrayList<>();
            dbServerList.add(dbServer);
            ROLE_LIST_MAP.put(dbRole, dbServerList);
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
    public DbServer getReader() {
        try {
            // 读取请求需要增加负载检测模块
            List<DbServer> serverFollowerList = ROLE_LIST_MAP.containsKey(Role.FOLLOWER) ? ROLE_LIST_MAP.get(Role.FOLLOWER).stream()
                    .filter(DbServer::isStatus).collect(Collectors.toList()) : new ArrayList<>();
            List<DbServer> serverReadReplicaList = ROLE_LIST_MAP.containsKey(Role.READ_REPLICA) ? ROLE_LIST_MAP.get(Role.READ_REPLICA).stream()
                    .filter(DbServer::isStatus).collect(Collectors.toList()) : new ArrayList<>();
            List<DbServer> serverLeaderList = ROLE_LIST_MAP.containsKey(Role.LEADER) ? ROLE_LIST_MAP.get(Role.LEADER).stream()
                    .filter(DbServer::isStatus).collect(Collectors.toList()) : new ArrayList<>();

            serverReadReplicaList.addAll(serverFollowerList);
            serverReadReplicaList.addAll(serverLeaderList);
            // 根据QUERY_COUNT倒排列表
            List<DbServer> dbServerList = serverReadReplicaList.stream()
                    .sorted(Comparator.comparingInt(DbServer::getQueryCount)).collect(Collectors.toList());

            return !dbServerList.isEmpty() ? dbServerList.get(0) : null;
        } catch (Exception e) {
            LOGGER.error("Get reader error!");
        }
        return null;
    }

    public String getReaderHttp() {
        return getReader() != null ? getReader().getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getReaderBlot() {
        return getReader() != null ? getReader().getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getReaderHttpMappingLocal() {
        return getReader() != null ? getReader().getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
    }

    public String getReaderBlotMappingLocal() {
        return getReader() != null ? getReader().getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取注册的Writer节点 - 获取失败执行重试逻辑 ( 包括执行重新注册心跳检测机制)
     */
    public DbServer getWriter() {
        // 写入请求只支持LEADER节点
        try {
            return ROLE_LIST_MAP.containsKey(Role.LEADER) ? ROLE_LIST_MAP.get(Role.LEADER).stream()
                    .filter(DbServer::isStatus).collect(Collectors.toList()).get(0) : null;
        } catch (Exception e) {
            LOGGER.error("Get writer error!");
        }
        return null;
    }

    public String getWriterHttp() {
        return getWriter() != null ? getWriter().getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getWriterBlot() {
        return getWriter() != null ? getWriter().getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getWriterHttpMappingLocal() {
        return getWriter() != null ? getWriter().getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
    }

    public String getWriterBlotMappingLocal() {
        return getWriter() != null ? getWriter().getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
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

