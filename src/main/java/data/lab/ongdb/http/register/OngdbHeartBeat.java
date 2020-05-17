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
import data.lab.ongdb.http.util.TelnetUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.net.ServerAddress;

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

    private static Map<String, String> HOST_MAP = new HashMap<>();

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
    public static HttpProxyRequest request;
    public static HttpRequest originalRequest;

    /**
     * 默认未注册心跳检测机制
     **/
    private static boolean IS_REGISTER = false;

    private String[] servers;

    /**
     * 执行一次节点列表ROLE MAP分类-单位秒-每隔几秒检测一次 - 默认5秒检测一次
     * 节点角色监控
     * 检查节点负载情况
     * 移除无效状态的DB SERVER
     **/
    private int DELAY = 5;

    /**
     * 心跳检测默认每3秒执行一次
     **/
    private int HEART_HEALTH = 3;

    /**
     * 心跳检测超时时间，默认10秒
     **/
    private int TIME_OUT = 10;

    /**
     * 监控线程初始执行延迟设置
     **/
    private static final long INITIAL_DELAY = 1;

    /**
     * 是否打印集群路由信息
     **/
    public static boolean IS_PRINT_CLUSTER_INFO = false;

    /**
     * 是否自动添加BLOT驱动
     **/
    public static boolean IS_ADD_BLOT_DRIVER = false;

    private String authAccount;

    private String authPassword;

    /**
     * 默认的事务超时时间设置10分钟
     **/
    private int withMaxTransactionRetryTime = 600;

    /**
     * 全局ROUTING DRIVER
     **/
    private static Driver routingDriverServer;

    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword) {
        new OngdbHeartBeat(ipPorts, authAccount, authPassword, this.DELAY);
    }

    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword, int delay) {
        new OngdbHeartBeat(ipPorts, authAccount, authPassword, delay, this.withMaxTransactionRetryTime);
    }

    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword, int delay, int withMaxTransactionRetryTime) {
        new OngdbHeartBeat(ipPorts, authAccount, authPassword, delay, withMaxTransactionRetryTime, this.HEART_HEALTH);
    }

    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword, int delay, int withMaxTransactionRetryTime, int heartHealthDetect) {
        new OngdbHeartBeat(ipPorts, authAccount, authPassword, delay, withMaxTransactionRetryTime, heartHealthDetect, this.TIME_OUT);
    }

    /**
     * @param ipPorts:'|'分隔的HOST:PORT地址
     * @param authAccount:用户名
     * @param authPassword:密码
     * @param delay:监控线程运行间隔（秒）
     * @param withMaxTransactionRetryTime:事务提交超时时间设置（秒）
     * @param heartHealthDetect:心跳检测间隔时间（秒）
     * @param timeOut:心跳检测超时时间（秒）
     * @return
     * @Description: TODO
     */
    public OngdbHeartBeat(String ipPorts, String authAccount, String authPassword, int delay, int withMaxTransactionRetryTime, int heartHealthDetect, int timeOut) {

        this.servers = Objects.requireNonNull(ipPorts).split(Symbol.SPLIT_CHARACTER.getSymbolValue());

        if (HOST_MAP.isEmpty()) {
            throw new IllegalArgumentException();
        }

        IS_REGISTER = true;
        this.authAccount = authAccount;
        this.authPassword = authPassword;

        this.DELAY = delay;
        this.withMaxTransactionRetryTime = withMaxTransactionRetryTime;
        this.HEART_HEALTH = heartHealthDetect;
        this.TIME_OUT = timeOut;

        HttpProxyRegister.register(getIpPortsStr(ipPorts), authAccount, authPassword);
        request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue(), authAccount, authPassword);
        originalRequest = new HttpRequest(authAccount, authPassword);

        // 多节点运行监控线程
        if (HOST_MAP.size() > 1) {
            // 初始化运行
            initRun();

            // 添加BLOT驱动
            addGraphJavaDriver();

            // 线程池运行
            threadPoolRun();
        } else {
            // 单节点不运行监控线程
            packDefaultHost();
            // 单点连接驱动
            LOGGER.info("ADD single node blot driver...");
            addBlotDriver();
        }
    }

    private void addGraphJavaDriver() {
        if (IS_ADD_BLOT_DRIVER) {
            // 自动路由驱动
            if (isIpAddress()) {
                LOGGER.info("ADD multi node blot routing driver...");
                addRoutingBlotDriver();
            } else {
                // 单点连接驱动
                LOGGER.info("ADD single node blot driver...");
                addBlotDriver();
            }
        }
    }

    private boolean isIpAddress() {
        int length = this.servers.length;
        for (int i = 0; i < length; i++) {
            if (i == length - 1) {
                return isIp(this.servers[i]);
            } else if (!isIp(this.servers[i])) {
                return false;
            }
        }
        return false;
    }

    private boolean isIp(String host) {
        String[] array = host.split(Symbol.POINT.getSymbolValue());
        return array.length == 4;
    }

    private void initRun() {
        // 执行一次节点列表ROLE MAP分类
        classifyNode();

        // 节点角色监控
        validCheck();

        // 检查节点负载情况
        checkTheLoad();

        // 执行一次心跳检测
        heartHealthdDetect();

        // 移除无效状态的DB SERVER
        removeNotValid();
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行心跳检测)
     */
    private void heartHealthdDetect() {
        if (IS_PRINT_CLUSTER_INFO) {
            LOGGER.info("Heart health check...");
        }
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                // HTTP地址
                Address httpAddress = getHttpAddress(server);
                boolean isConnected = TelnetUtil.telnet(getHost(httpAddress.getHost()), httpAddress.getPort(), TIME_OUT);
                server.setStatus(isConnected);
            }
        }
    }

    private void addRoutingBlotDriver() {
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();

        List<ServerAddress> addresses = getServerAddress();
        String virtualUri = addresses.get(0).host();
        try {
            for (List<DbServer> dbServerList : dbServerCollection) {
                for (DbServer server : dbServerList) {
                    // 不存在驱动则添加
                    if (server.getRoutingDriverServerAddress() == null) {
                        if (routingDriverServer == null) {
                            routingDriverServer = createDriverCluster(AccessPrefix.SINGLE_NODE.getSymbol() + virtualUri, authAccount, authPassword, addresses);
                        }
                        server.setRoutingDriverServerAddress(routingDriverServer);
                        server.setDriverServerAddressMappingLocal(routingDriverServer);
                        server.setDriverServerAddress(routingDriverServer);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Add routing driver error!" + e);
        }
    }

    private List<ServerAddress> getServerAddress() {
        List<ServerAddress> addresses = new ArrayList<>();
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                List<Address> addressList = server.getAddressList();
                for (Address address : addressList) {
                    if (Protocol.BLOT.equals(address.getProtocol()) &&
                            !addresses.contains(ServerAddress.of(address.getInitHost(), address.getPort()))) {
                        addresses.add(ServerAddress.of(address.getInitHost(), address.getPort()));
                    }
                }
            }
        }
        return addresses;
    }

    private Driver createDriverCluster(String virtualUri, String user, String password, ServerAddress... addresses) {
        Config config = Config.builder()
                // 多种子连接配置
                .withResolver(address -> new HashSet<>(Arrays.asList(addresses)))
                // 事务超时时间设置
                .withMaxTransactionRetryTime(withMaxTransactionRetryTime, TimeUnit.SECONDS)
                .build();
        return (GraphDatabase.driver(virtualUri, AuthTokens.basic(user, password), config));
    }

    private Driver createDriverCluster(String virtualUri, String user, String password, List<ServerAddress> addresses) {
        Config config = Config.builder()
                // 多种子连接配置
                .withResolver(address -> new HashSet<>(addresses))
                // 事务超时时间设置
                .withMaxTransactionRetryTime(withMaxTransactionRetryTime, TimeUnit.SECONDS)
                .build();
        return (GraphDatabase.driver(virtualUri, AuthTokens.basic(user, password), config));
    }

    /**
     * @param
     * @return
     * @Description: TODO(ROLE_LIST_MAP添加驱动)
     */
    private void addBlotDriver() {
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                Address address = getHttpAddress(server.getAddressList());
                // 不存在驱动则添加
                if (server.getDriverServerAddressMappingLocal() == null) {
                    try {
                        // 本地映射
                        String serverAddressMappingLocal = address.getServerAddressMappingLocal();
                        Driver driverServerAddressMappingLocal = GraphDatabase.driver(AccessPrefix.SINGLE_NODE.getSymbol() + serverAddressMappingLocal, AuthTokens.basic(authAccount, authPassword));
                        server.setDriverServerAddressMappingLocal(driverServerAddressMappingLocal);
                    } catch (Exception e) {
                        LOGGER.error("Add driver mapping local error!" + e);
                    }
                }
                // 不存在驱动则添加
                if (server.getDriverServerAddress() == null) {
                    try {
                        // 远程主机名
                        String serverAddress = address.getServerAddress();
                        Driver driverServerAddress = GraphDatabase.driver(AccessPrefix.SINGLE_NODE.getSymbol() + serverAddress, AuthTokens.basic(authAccount, authPassword));
                        server.setDriverServerAddress(driverServerAddress);
                    } catch (Exception e) {
                        LOGGER.error("Add remote driver error!");
                    }
                }
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(关闭DRIVER)
     */
    public static void closeDriver() {
        IS_ADD_BLOT_DRIVER = false;
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                if (server.getDriverServerAddress() != null) {
                    server.getDriverServerAddress().close();
                }
                if (server.getDriverServerAddressMappingLocal() != null) {
                    server.getDriverServerAddressMappingLocal().close();
                }
                if (server.getRoutingDriverServerAddress() != null) {
                    server.getRoutingDriverServerAddress().close();
                }
                server.setDriverServerAddressMappingLocal(null);
                server.setRoutingDriverServerAddress(null);
            }
        }
        closeHttp();
    }

    private static void closeHttp() {
        LOGGER.info("Close http and java driver!");
        request = null;
        originalRequest = null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(关闭DRIVER)
     */
    public static void closeDriverAsync() {
        IS_ADD_BLOT_DRIVER = false;
        Collection<CopyOnWriteArrayList<DbServer>> dbServerCollection = ROLE_LIST_MAP.values();
        for (List<DbServer> dbServerList : dbServerCollection) {
            for (DbServer server : dbServerList) {
                if (server.getDriverServerAddress() != null) {
                    server.getDriverServerAddress().closeAsync();
                }
                if (server.getDriverServerAddressMappingLocal() != null) {
                    server.getDriverServerAddressMappingLocal().closeAsync();
                }
                if (server.getRoutingDriverServerAddress() != null) {
                    server.getRoutingDriverServerAddress().closeAsync();
                }
                server.setDriverServerAddress(null);
                server.setDriverServerAddressMappingLocal(null);
                server.setRoutingDriverServerAddress(null);
            }
        }
        closeHttp();
    }

    private static Address getHttpAddress(List<Address> addressList) {
        for (Address address : addressList) {
            if (Protocol.BLOT.equals(address.getProtocol())) {
                return address;
            }
        }
        return new Address("127.0.0.1", 7687, Protocol.BLOT, "localhost", true);
    }

    /**
     * @return
     * @Description: TODO(运行集群心跳检测机制 - 每隔一段事件检查一遍集群路由节点)(监控集群节点列表的变化 - 并添加到ROLE MAP LIST中)
     */
    private void threadPoolRun() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::classifyNode, INITIAL_DELAY, this.DELAY, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::validCheck, INITIAL_DELAY, this.DELAY, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::checkTheLoad, INITIAL_DELAY, this.DELAY, TimeUnit.SECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::removeNotValid, INITIAL_DELAY, this.DELAY, TimeUnit.SECONDS);
        if (IS_ADD_BLOT_DRIVER) {
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                    this::addGraphJavaDriver, INITIAL_DELAY, this.DELAY, TimeUnit.SECONDS);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                this::removeNotValid, INITIAL_DELAY, this.HEART_HEALTH, TimeUnit.SECONDS);
        LOGGER.info("ONgDB heartbeat detection run interval:" + this.HEART_HEALTH + "s " + "ongdb.heartbeat.detection.interval:" + this.DELAY + "s");
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
            String singleNodeConf = request.httpPost("/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());
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

    /**
     * @param
     * @return
     * @Description: TODO(KEY是远程节点的主机名 ， VALUE是可以访问远程节点的主机名或者IP)
     */
    public static void setHostMap(Map<String, String> hostMap) {
        HOST_MAP = hostMap;
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
                    String queryCountStr = originalRequest.httpPost("http://" + ipPort + "/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());
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
                    e.printStackTrace();
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
                    String clusterViewStr = originalRequest.httpPost("http://" + ipPort + "/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());

                    Role currentRole = getRoleFromResult(clusterViewStr);
                    if (!role.equals(currentRole)) {
                        server.setStatus(false);
                    }
                } catch (Exception e) {
                    LOGGER.error("Valid check fail:" + server.toString() + e);
                    e.printStackTrace();
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
                String clusterViewStr = request.httpPost("/" + NeoUrl.DB_DATA_TRANSACTION_COMMIT.getSymbolValue(), condition.toString());

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
                LOGGER.error("All node connect refused:" + ipPort);
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
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getReaderBlot() {
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getReaderHttpMappingLocal() {
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
    }

    public String getReaderBlotMappingLocal() {
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getAddressList()
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
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getWriterBlot() {
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.BLOT.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddress() : null;
    }

    public String getWriterHttpMappingLocal() {
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getAddressList()
                .stream()
                .filter(v -> Protocol.HTTP.equals(v.getProtocol()))
                .collect(Collectors.toList())
                .get(0).getServerAddressMappingLocal() : null;
    }

    public String getWriterBlotMappingLocal() {
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getAddressList()
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
    public static boolean isRegister() {
        return IS_REGISTER;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取集群路由信息)
     */
    public Map<Role, CopyOnWriteArrayList<DbServer>> getRoleListMap() {
        return ROLE_LIST_MAP;
    }

    public Driver getReaderBlotDriver() {
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getDriverServerAddress() : null;
    }

    public Driver getReaderBlotMappingLocalDriver() {
        DbServer dbServer = getReader();
        return dbServer != null ? dbServer.getDriverServerAddressMappingLocal() : null;
    }

    public Driver getWriterBlotDriver() {
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getDriverServerAddress() : null;
    }

    public Driver getWriterBlotMappingLocalDriver() {
        DbServer dbServer = getWriter();
        return dbServer != null ? dbServer.getDriverServerAddressMappingLocal() : null;
    }
}

