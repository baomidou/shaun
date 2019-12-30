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
        <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-1.5+_2.0+-green.svg" >
    </a>
</p>

# 特性:
1. 功能简单，易于集成。
2. 无 session。
3. 使用凭证(token) 进行身份验证(默认是 jwt)。
4. 前后端不分离下,能依托pac4j的各种client快速集成三方登录(redirect跳转那种),例如oauth(qq,微信) 和 cas。

模块简介:
- shaun-core: 核心包
- shaun-spring-boot-starter: spring boot 快速启动包
- shaun-test-cookie: 前后端不分离下的测试演示
- shaun-test-stateless-cookie: 前后端分离下使用cookie存token的测试演示
- shaun-test-stateless-header: 前后端分离下使用request header携带token的测试演示

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

```java
@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final SecurityManager securityManager;

    @Override
    @Transactional
    public String login() {
        // 登录成功后把用户角色权限信息存储到profile中
        final JwtProfile profile = new JwtProfile();
        profile.setId(userId.toString());
        if (roles.contains(AdminConst.SUPER_ADMIN)) {
            isAdmin = true;
        } else {
            profile.setPermissions(permissionService.selectPermissionsByUserId(userId).stream()
                    .filter(x -> Objects.nonNull(x.getCode())).map(SysPermission::getCode).collect(Collectors.toSet()));
            profile.setRoles(new HashSet<>(roles));
        }
        final String token = securityManager.login(profile, isAdmin);
        return token;
    }
```

3. 设置yml启动信息。

```yaml
shaun:
  salt: d614a4fdff6540c1a5b730afc5f9cc8f #非必须
  exclude-path:
    - /v2/api-docs
    - /swagger-resources
    - /doc.html
  exclude-branch:
    - /wechat-auth
    - /webjars
```

4. 注解拦截。

类似于shiro，shaun也默认支持使用注解在controller上拦截。

相关的注解有 `@HasAuthorization`   `@HasPermission`  `@HasRole`  。

```java
@HasPermission(value = {"add","edit"},logical = Logical.BOTH) //同时存在
@HasPermission(value = {"add","edit"},logical = Logical.ANY)  //任一存在
```

5. 前后端交互。

默认配置下  前端登录后需要把后端返回的token存下，后续接口的请求头带上Authorization。

后端可以通过  TokenProfile profile = ProfileHolder.getProfile();  获得用户信息。