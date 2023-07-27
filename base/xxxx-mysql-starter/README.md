### feat
- 引入 `spring-boot-starter-jdbc` 和 `mybatis-plus-boot-starter` 依賴；
- 字段加解密；

#### 加解密用法
利用 MyBatis 的插件拦截参数和结果集的映射，对添加注解的实体字段加密和解密，也可以主动使用 `CryptoUtils` 工具类加密和解密。

`Crypto`
> 复合注解，可修饰类和类的字段；添加在类上，标注该类需要加密和解密，同时需要在特定字段上添加该注解，这样程序在使用 MyBatis 和数据库交互时会对这些字段做加密和解密处理。

`Decryption`
> 解密注解，可修饰类的字段和方法返回结果；添加在类字段上，表示该字段只需要解密；添加在方法返回结果上，程序在使用 MyBatis 和数据库交互时会对参数中添加`Crypto` 或 `Decryption` 注解的字段做解密处理。

`Encryption`
> 加密注解，可修饰类的字段和方法参数；添加在类字段上，表示该字段只需要加密；添加在方法参数上，程序在使用 MyBatis 和数据库交互时会对参数中添加`Crypto` 或 `Encryption` 注解的字段做加密处理。

使用样例可参看 `xxxx-demo` 模块。