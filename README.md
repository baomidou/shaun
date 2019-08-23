## 一个基于 [pac4j](https://github.com/pac4j/pac4j) 的安全框架

### 特点:
1. 简单
2. 无 session
3. 使用凭证(token) 进行身份验证(默认是 jwt)
4. 前后端不分离下,能依托pac4j的各种client快速集成三方登录(redirect跳转那种),例如oauth(qq,微信) 和 cas

模块简介:
- shaun-core: 核心包
- shaun-spring-boot-starter: spring boot 快速启动包
- shaun-test-cookie: 前后端不分离下的测试演示
- shaun-test-stateless-cookie: 前后端分离下使用cookie存token的测试演示
- shaun-test-stateless-header: 前后端分离下使用request header携带token的测试演示