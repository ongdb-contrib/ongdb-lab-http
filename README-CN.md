# ONgDB-LAB-HTTP
## 包含的功能有：
- http连接池
- http服务调用组件-HttpRequestUtil
- http负载均衡组件-HttpRequestProxy
## 负载均衡组件特点：
- 1.服务负载均衡（目前提供RoundRobin负载算法）
- 2.服务健康检查
- 3.服务容灾故障恢复
- 4.服务自动发现（zk，etcd，consul，eureka，db，其他第三方注册中心）
- 5.分组服务管理
 >可以配置多组服务集群地址，每一组地址清单支持的配置格式：http://ip:port    https://ip:port    ip:port（默认http协议）    多个地址用逗号分隔
- 6.服务安全认证（配置basic账号和口令）
- 7.主备路由/异地灾备特色
 >负载均衡器主备功能开发，如果主节点全部挂掉，请求转发到可用的备用节点，如果备用节点也挂了，就抛出异常，如果主节点恢复正常，那么请求重新发往主节点 

## ONgDB
- 1.The Neo4j HTTP API Docs v3.5:https://neo4j.com/docs/http-api/3.5/
- 2.https://neo4j.com/docs/#http-api-transactional
- 3.https://neo4j.com/docs/rest-docs/current/
- 4.注册节点地址
- 5.节点发现
- 6.读写分离
- 7.负载均衡
- 8.自动路由
- 9.主机名域名映射配置
