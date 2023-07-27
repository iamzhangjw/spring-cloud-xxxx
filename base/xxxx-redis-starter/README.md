### feat
- 引入 `spring-boot-starter-data-redis`、`spring-boot-starter-cache` 和 `caffeine` 依賴；
- 实现本地缓存及 Redis 远程缓存二级缓存；


#### 二级缓存
使用 `caffeine` 做一级缓存，默认有效时间为 10 分钟，优先使用一级缓存；二级缓存使用 Redis，默认有效时间为 2 小时。
支持自定义缓存规则，使用样例见 `xxxx-demo` 模块。
