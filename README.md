<p align="center">

</p>

<p align="center">
	<strong>一个基于pac4j的安全框架</strong>
</p>

<p align="center">
    <a href="https://www.travis-ci.org/baomidou/shaun-spring-boot-starter" target="_blank">
        <img src="https://www.travis-ci.org/baomidou/shaun-spring-boot-starter.svg?branch=master" >
    <a href="http://mvnrepository.com/artifact/com.baomidou/shaun-spring-boot-starter" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.baomidou/shaun-spring-boot-starter.svg" >
    </a>
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="http://img.shields.io/:license-apache-brightgreen.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-1.8.0_211+-green.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-2.0+-green.svg" >
    </a>
</p>

# 特性:
1. 功能简单，易于集成。
2. 无 session。
3. 使用凭证(token) 进行身份验证(默认是 jwt)。
4. 前后端不分离下,能依托pac4j的各种client快速集成三方登录(redirect跳转那种),例如oauth(qq,微信) 和 cas。

# 模块简介

- shaun-core: 核心包。
- shaun-spring-boot-starter: spring boot 快速启动包。
- tests下: 各种测试演示。

# 使用方法

1. 引入shaun-spring-boot-starter。

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>shaun-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

2. 登录后设置相关信息到SecurityManager。

``` java
@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final SecurityManager securityManager;

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
        return token;
    }
```

3. 设置yml启动信息。

``` yaml
shaun:
  salt: 32位字符串,不配置则每次启动不一样导致重启前登录的用户token失效
  exclude-path: # 排除具体的路径
    - /v2/api-docs  
    - /swagger-resources
    - /doc.html
  exclude-branch: # 排除以此路径开头
    - /webjars
  expire-time: '1d' # 不设置默认永久有效
  
 # jwt 超时时间 10s : 表示10秒内有效
 #             10m 结尾: 表示10分钟内有效
 #             10h 结尾: 表示10小时内有效
 #             1d : 表示有效时间截止当天结束
 #             2d1h : 表示有效时间在明天的 01:00:00 前有效 
 #             d 后面 只支持上面三个(`s`,`m`,`h`)之一
```

4. 注解拦截。

类似于shiro，shaun也默认支持使用注解在controller上拦截。

相关的注解有 `@HasAuthorization`   `@HasPermission`  `@HasRole`  。

``` java
@HasPermission(value = {"add","edit"},logical = Logical.BOTH) //权限必须同时存在
@HasPermission(value = {"add","edit"},logical = Logical.ANY)  //权限任一存在(默认)
```

5. 前后端交互。

默认配置下  前端登录后需要把后端返回的token存下，后续接口的请求头带上Authorization。

后端可以通过  TokenProfile profile = ProfileHolder.getProfile();  获得登录的用户信息。

如获得登录设置进token里的用户ID继续进行业务逻辑处理。

如   `Long id= ProfileHolder.getProfile().getId();`