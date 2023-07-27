### 写在前面

project 依托 [Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba) 2.2.x 版本搭建，遵循 [Oauth 2.0 标准](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html) 实现了验证和授权流程，在 gateway 和 业务 module 中对响应结构做了统一处理，限流、降级、全局异常等响应也保持一致。

对于企业实践常用的数据源及组件都封装成基础 module，按需加入业务 module 即可使用，后续还会视实际情况引入更多组件简化开发和运维，打造一个开箱即用的脚手架项目。



### 准备工作

由于大部分后端项目和开发人员使用 maven，故本 project 也采用 maven 管理项目生命周期，因此对于 JDK 和 apache maven 的安装及配置就不在此赘述，JDK 版本为 1.8，[maven 版本](https://maven.apache.org/download.cgi) 选择最新的稳定版本即可。

下面介绍 project 使用到的组件或服务安装及配置。

#### MySQL
安装 [最新稳定版本](https://dev.mysql.com/downloads/mysql/) 即可。

#### Nacos
在 Spring Cloud Alibaba 中，我们一般使用 nacos 作为微服务注册、发现和管理组件，同时也可以作为微服务的配置管理，详细文档可参看 [官网](https://nacos.io/zh-cn/docs/what-is-nacos.html) 。

从 [最新稳定版本](https://github.com/alibaba/nacos/releases) 下载压缩包 nacos-server-$version.zip，解压到任意目录；进入 bin 目录，打开启动服务的脚本文件：
> Windows 下为 `startup.cmd`，Linux/Unix 下为 `startup.sh`。

找到 `MODE="cluster"` 行，修改为 `MODE="standalone"`，保存后执行该文件，服务就以单机模式运行。

nacos 默认使用嵌入式数据库存储数据，重启后会丢失数据，可以修改 conf 下的配置文件 `application.properties`，让 nacos 使用 MySQL 存储数据。示例如下：
```properties
### Count of DB:
db.num=1

### Connect URL of DB:
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=root
db.password.0=123456
```

**同时需要创建 `nacos` 数据库，并执行 project 下 *ddl* 目录下的 `nacos.sql` 脚本。**


nacos 配置参数可参看 [Nacos 系统参数介绍](https://nacos.io/zh-cn/docs/system-configurations.html) 。

启动服务后，[点击](http://127.0.0.1:8848/nacos/) 访问控制台，默认用户名密码为 `nacos/nacos`。

#### Sentinel
引入 Sentinel 从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性，详细文档可参看 [wiki](https://github.com/alibaba/Sentinel/wiki/介绍) 。

我们可以从 [release 页面](https://github.com/alibaba/Sentinel/releases) 下载最新版本的控制台 jar 包 sentinel-dashboard-$version.jar。
使用如下命令启动控制台：

```cmd
java -Dserver.port=8888 -Dcsp.sentinel.dashboard.server=127.0.0.1:8888 -Dproject.name=sentinel-dashboard -jar -Dsentinel.dashboard.auth.username=sentinel -Dsentinel.dashboard.auth.password=123456 sentinel-dashboard-1.8.2.jar
```

我们在启动命令中指定了控制台端口为 **8888**，登录用户名密码为 `sentinel/123456`，[点击](http://127.0.0.1:8888/) 访问控制台。

#### Redis
安装 [最新稳定版本](https://redis.io/download) 即可。

#### MongoDB
安装 [最新稳定版本](https://www.mongodb.com/try/download/community) 即可。

#### Elasticsearch
安装 [最新稳定版本](https://www.elastic.co/cn/downloads/elasticsearch) 即可，最好安装 7.x。配置文件 `conf/elasticsearch.yml` 示例如下：
```yaml
# 集群名
cluster.name: elasticsearch
# 节点名
node.name: node-1
# 数据目录
path.data: D:\dre\elasticsearch-7.15.1\data
# 日志目录
path.logs: D:\dre\elasticsearch-7.15.1\logs
# 外部可访问的网卡主机，配置威 0.0.0.0 所有网卡网络都可以访问
network.host: 0.0.0.0
# 对外端口
http.port: 9200
# 跨域，便于 SkyWalking 访问
http.cors.enabled: true
http.cors.allow-origin: "*"
# 初始化主节点，启动程序后会注册到该节点上
cluster.initial_master_nodes: ["node-1"]
```

启动成功后，在浏览器中打开 `http://127.0.0.1:9200/` 访问；也可以安装插件 head 直观的监控 elasticsearch。

#### SkyWalking
安装版本 [8.7.0](https://archive.apache.org/dist/skywalking/8.7.0/apache-skywalking-apm-es7-8.7.0.tar.gz)，对应 Elasticsearch 7.x，配置文件 `config/application.yml` 示例如下：
```yaml
cluster:
  selector: nacos
  # 省略无关配置
  nacos:
    serviceName: SkyWalking
    hostPort: 127.0.0.1:8848
    # Nacos Configuration namespace
    namespace: xxxx
    # Nacos auth username
    username: nacos
    password: nacos
    # Nacos auth accessKey
    accessKey: ${SW_CLUSTER_NACOS_ACCESSKEY:""}
    secretKey: ${SW_CLUSTER_NACOS_SECRETKEY:""}

storage:
  selector: elasticsearch7
  # 省略无关配置
  elasticsearch7:
    nameSpace: elasticsearch
    clusterNodes: 127.0.0.1:9200
    protocol: ${SW_STORAGE_ES_HTTP_PROTOCOL:"http"}
    connectTimeout: ${SW_STORAGE_ES_CONNECT_TIMEOUT:500}
    socketTimeout: ${SW_STORAGE_ES_SOCKET_TIMEOUT:30000}
    trustStorePath: ${SW_STORAGE_ES_SSL_JKS_PATH:""}
    trustStorePass: ${SW_STORAGE_ES_SSL_JKS_PASS:""}
    dayStep: ${SW_STORAGE_DAY_STEP:1} # Represent the number of days in the one minute/hour/day index.
    indexShardsNumber: 1 # Shard number of new indexes
    indexReplicasNumber: 0 # Replicas number of new indexes
    # Super data set has been defined in the codes, such as trace segments.The following 3 config would be improve es performance when storage super size data in es.
    superDatasetDayStep: ${SW_SUPERDATASET_STORAGE_DAY_STEP:-1} # Represent the number of days in the super size dataset record index, the default value is the same as dayStep when the value is less than 0
    superDatasetIndexShardsFactor: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_SHARDS_FACTOR:5} #  This factor provides more shards for the super data set, shards number = indexShardsNumber * superDatasetIndexShardsFactor. Also, this factor effects Zipkin and Jaeger traces.
    superDatasetIndexReplicasNumber: ${SW_STORAGE_ES_SUPER_DATASET_INDEX_REPLICAS_NUMBER:0} # Represent the replicas number in the super size dataset record index, the default value is 0.
    indexTemplateOrder: ${SW_STORAGE_ES_INDEX_TEMPLATE_ORDER:0} # the order of index template
    user: ${SW_ES_USER:""}
    password: ${SW_ES_PASSWORD:""}
    secretsManagementFile: ${SW_ES_SECRETS_MANAGEMENT_FILE:""} # Secrets management file in the properties format includes the username, password, which are managed by 3rd party tool.
    bulkActions: ${SW_STORAGE_ES_BULK_ACTIONS:5000} # Execute the async bulk record data every ${SW_STORAGE_ES_BULK_ACTIONS} requests
    # flush the bulk every 10 seconds whatever the number of requests
    # INT(flushInterval * 2/3) would be used for index refresh period.
    flushInterval: ${SW_STORAGE_ES_FLUSH_INTERVAL:15}
    concurrentRequests: ${SW_STORAGE_ES_CONCURRENT_REQUESTS:2} # the number of concurrent requests
    resultWindowMaxSize: ${SW_STORAGE_ES_QUERY_MAX_WINDOW_SIZE:10000}
    metadataQueryMaxSize: ${SW_STORAGE_ES_QUERY_MAX_SIZE:5000}
    segmentQueryMaxSize: ${SW_STORAGE_ES_QUERY_SEGMENT_SIZE:200}
    profileTaskQueryMaxSize: ${SW_STORAGE_ES_QUERY_PROFILE_TASK_SIZE:200}
    oapAnalyzer: ${SW_STORAGE_ES_OAP_ANALYZER:"{\"analyzer\":{\"oap_analyzer\":{\"type\":\"stop\"}}}"} # the oap analyzer.
    oapLogAnalyzer: ${SW_STORAGE_ES_OAP_LOG_ANALYZER:"{\"analyzer\":{\"oap_log_analyzer\":{\"type\":\"standard\"}}}"} # the oap log analyzer. It could be customized by the ES analyzer configuration to support more language log formats, such as Chinese log, Japanese log and etc.
    advanced: ${SW_STORAGE_ES_ADVANCED:""}
```

启动后，浏览器访问 `http://127.0.0.1:8080` 即可，如果想改改端口，打开 `webapp/webapp.yml` 编辑。
> SkyWalking 默认不支持 gateway 链路追踪，因此需要把 `agent/optional-plugins/apm-spring-cloud-gateway-2.1.x-plugin-8.7.0.jar` 复制到 `agent/plugins` 下，然后重启即可。

*如果想对日志的采集做自定义解析，请参考 [How To Use Apache Skywalking Log Analysis Language To Process Logs](https://www.youtube.com/watch?v=-FfI0DPwqjA&ab_channel=TheApacheFoundation) 。*

### 设计思路

project 初步分为 base、business 和 xxxx-gateway 三部分；顾名思义，`base` 下的 module 包括且不限于开发业务中使用到的工具及组件依赖的简单封装，如各种存储依赖、授权客户端、MyBatis 代码生成等，`business` 是实际开发业务的微服务集合，`xxxx-gateway` 是对 `spring-cloud-gateway` 的自定义封装；具体各个 module 的主要功能接下来会一一做说明，*如果添加新的 module 可按照我的定义对号入座*。

#### base

| 服务名 | 依赖组件 | 主要功能 | 备注 |
| ------- | ------- | ------- | ------- |
|   mbp-generator   |   无   | 生成指定表的 xml、mapper、service 和 controller 文件 |         |
|   xxxx-foundation   |   无   | 常用工具类、公共异常、公共 entity 等 |         |
|   xxxx-web-starter   | nacos、sentinel、 xxxx-foundation | 封装 spring boot web 应用需要的所有功能，包括数据脱敏、全局异常处理、熔断异常处理、统一响应体、feign 调用时自定义 header 传递、登录用户上下文、swagger 集成、跨域、SkyWalking 请求追踪和日志采集等 | 不支持 WebFlux |
|   xxxx-mongodb-starter   | mongodb | mongodb 集成及 Template 封装 |         |
|   xxxx-redis-starter   | redis | redis 集成及 Template 封装、本地缓存和基于 redis 实现的分布式缓存 |         |
|   xxxx-mysql-starter   |  mysql | mysql 集成及 MyBatis-Plus 引入、敏感数据加密存储 |         |
|   xxxx-oauth-client   | xxxx-web-starter | 对所有访问请求鉴权，可在配置文件中指定排除 uri | 一般业务服务直接引入依赖即可 |

#### business
`business` 下的 module 默认添加了 `xxxx-web-starter` 和 `xxxx-mysql-starter` 依賴，一般业务相关的微服务都在这里。

| 服务名 | 端口 | 依赖组件 | 主要功能 | 备注 |
| ------- | ------- | ------- | ------- | ------- |
|   xxxx-demo   |   9001   | xxxx-web-starter、xxxx-mysql-starter、xxxx-redis-starter、xxxx-oauth-client | 示例工程 |         |
|   xxxx-oauth-server   |   9002   | xxxx-web-starter、xxxx-mysql-starter、xxxx-redis-starter | 认证和授权服务 |         |
|   xxxx-job   |   9003   | xxxx-web-starter、xxxx-mysql-starter、xxxx-oauth-client | 定时任务服务 |         |
|   xxxx-websocket   |   9004   | xxxx-web-starter、xxxx-mysql-starter、xxxx-oauth-client | web socket 服务 |         |

#### xxxx-gateway
微服务网关，本服务依賴 `redis` 和 `xxxx-oauth-client` ，因为 `spring-cloud-gateway` 是基于 `spring-boot-webflux` 构建，因此和 `spring-boot-web` 不兼容，我们在 `xxxx-web-starter` 中做的熔断降级处理和响应包装不适用，快速开发流程可参考 [Spring WebFlux 入门](https://www.cnblogs.com/cjsblog/p/12580518.html) 。gateway 对整个微服务的所有请求做了鉴权、路由转发、熔断限流和跨域，登录、验证、鉴权等接口例外。



### 如何开始
废话说了这么多，我们最关心的还是怎么开发自己的业务程序，我们可以根据实际情况从业务范围、服务承载能力等维度出发创建或拆分新的微服务。
比如我们需要为指挥调度业务创建一个名为 `xxxx-partol` 的微服务，放在 `module` 分类下，很显然这个业务需要鉴权，那么就可以在 dependencies 中加入 `xxxx-oauth-client` 依賴，由于 `business` 模块默认添加了 `xxxx-mysql-starter`  依賴，子 module 会继承父 module 的依賴，如果服务中只使用到 MySQL 的话就不需要重复添加了，当有其他组件依賴时可在 `base` 下查看是否存在并按需添加，如果没有也可以自己在 `base` 下创建新的 starter。
> 注意：启动类的 `@EnableFeignClients` 和 `@SpringBootApplication` 注解默认指挥扫描所在类的统计 package 及子级 package 下的 bean，所以引入了不同的依賴后需添加对应 module 的代码的 package。

`xxxx-demo` 中有一些 examples 可供参考，如果有新的组件引用也可以添加示例代码供后人快速了解。

如果启用链路追踪和日志采集，需要添加启动参数，这样就可以在 `SkyWalking` 中监控服务了。
- VM OPS
`-javaagent:D:\dre\apache-skywalking-apm-bin-es7\agent\skywalking-agent.jar`
> 采用代理方式启动 SkyWalking Agent，jar 包在 SkyWalking 解压后文件的 agent 目录下。
  
- 环境变量
`SW_AGENT_COLLECTOR_BACKEND_SERVICES=127.0.0.1:11800;SW_AGENT_NAME=xxxx-demo;plugin.toolkit.log.grpc.reporter.server_host=127.0.0.1;plugin.toolkit.log.grpc.reporter.server_port=11800`
> SW_AGENT_COLLECTOR_BACKEND_SERVICES SkyWalking 采集服务访问地址
> 
> SW_AGENT_NAME 代理服务名
> 
> plugin.toolkit.log.grpc.reporter.server_host SkyWalking 日志上报服务 host
> 
> plugin.toolkit.log.grpc.reporter.server_port SkyWalking 日志上报服务端口
>
> agent.instance_name 实例名，SkyWalking 会将相同的名称看做一个实例。如果为空，SkyWalking agent 会生成一个32位的 uuid，用 uuid@hostname 作为实例名

*更多 agent 属性可参考 [配置属性列表](https://skyapm.github.io/document-cn-translation-of-skywalking/zh/6.2.0/setup/service-agent/java-agent/) 。*