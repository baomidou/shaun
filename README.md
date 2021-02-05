<p align="center">

</p>

<p align="center">
	<strong>基于 pac4j-jwt 的 WEB 安全组件</strong>
</p>

<p align="center">
    <a href="http://mvnrepository.com/artifact/com.baomidou/shaun-spring-boot-starter" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.baomidou/shaun-spring-boot-starter.svg">
    </a>
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg">
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-1.8.0_211+-green.svg">
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-2.0+-green.svg">
    </a>
</p>

# 简介

主要依托 `pac4j-jwt` 来提供默认使用 `JWT` 的 WEB 安全组件

技术讨论 QQ 群: 183066216

# 优点

- 迅速集成,只需要少量配置+代码即可实现基本的接口防护
- 默认使用 `jwt` 进行身份认证
- 灵活的 `jwt` 配置,默认`签名`+`加密`
- 更多高级功能只需实现对应接口并注入到spring容器内
- 本框架各类均不会使用`session`(`pac4j`提供的类除外)
- 前后端不分离下,能依托`pac4j`的各种client快速集成三方登录(redirect跳转那种),例如oauth(qq,微信) 和 cas。

# 模块简介

- shaun-core: 核心包。
- shaun-togglz: 提供对 `togglz` 的 `UserProvider` 一个实现类
- shaun-spring-boot-starter: spring boot 快速启动包。
- tests下: 各种测试演示。

# 安装

1. 引入: shaun-spring-boot-starter 和 spring-boot-starter-web

``` xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>shaun-spring-boot-starter</artifactId>
    <version>Latest Version</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>spring-boot-version</version>
</dependency>
```

2. 配置 `application.yml`

```yaml
shaun:
  salt: 32位字符串,不配置则每次启动不一样导致重启前登录的用户token失效
  stateless: true # 默认 true,表示是前后端分离的
  session-on: false # 默认 false,表示不用session(会影响pac4j的类对session的操作)
  login-url: /login # 如果配置,则会加入 exclude-path 内,前后端不分离下鉴权失败会被重定向到此
  token-location: header # 默认 header,token 的存取位置
  exclude-path: # 排除具体的路径
    - /v2/api-docs
    - /swagger-resources
    - /doc.html
  exclude-branch: # 排除以此路径开头
    - /webjars
  expire-time: 1d # 不设置默认永久有效 
# 更多配置参见源码
```

> expire-time: jwt 有效时长(`d`后面只支持三个(`s`,`m`,`h`)之一)
>> 10s : 表示10秒内有效  
> > 10m 结尾: 表示10分钟内有效  
> > 10h 结尾: 表示10小时内有效  
> > 1d : 表示有效时间到第二天 00:00:00  
> > 2d1h : 表示有效时间到第三天 01:00:00

3. 编写登陆代码

``` java
import com.baomidou.shaun.core.mgt.SecurityManager;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SecurityManager securityManager;

    @Override
    @Transactional
    public String login() {
        // 登录成功后把用户角色权限信息存储到profile中
        final TokenProfile profile = new TokenProfile();
        profile.setId(userId.toString());
        //profile.addRole(role:String);  
        //profile.setRoles(roles:Set);  
        //profile.addPermission(permission:String);
        //profile.setPermissions(permissions:Set);
        //profile.addAttribute("key","value");
        final String token = securityManager.login(profile);
        //如果选择token存cookie里,securityManager.login会进行自动操作
        return token;
    }
```

4. 注解权限拦截:

> `@HasAuthorization` , `@HasPermission` , `@HasRole`
>> 支持注解在`method`上以及`class`上

例:

``` java
@HasPermission(value = {"add", "edit"}, logical = Logical.BOTH) //权限必须同时存在
@HasPermission(value = {"add", "edit"}, logical = Logical.ANY)  //权限任一存在(默认)
```

5. 如何获取用户信息(不需要安全拦截的接口获取不到哦)

```java
TokenProfile profile = ProfileHolder.getProfile();