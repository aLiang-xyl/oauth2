# auth2认证服务
基于springboot的oauth2 jwt，使用私钥进行加密，token信息保存在redis中。用户密码加密方式SCryptPasswordEncoder。

<a href="https://blog.csdn.net/qq_20280007/article/details/107388319" target="_blank">见博客</a>

## 公钥私钥生成方式:

**私钥**

用于auth2认证服务

```sh
keytool -genkey -alias auth-jwt -keypass 123456 -keyalg RSA -storetype PKCS12 -keysize 1024 -validity 365 -keystore auth-jwt.jks -storepass 123456  -dname "CN=(Felord), OU=(auth-jwt), O=(auth-jwt), L=(zz), ST=(hn), C=(cn)"
```

**公钥**

基于私钥生成公钥，用于auth2资源服务进行验签

使用下面的命令，将公钥信息保存在任意文本中，例如：public.pub  

```sh
keytool -list -rfc --keystore auth-jwt.jks | openssl x509 -inform pem -pubkey
```

## 配置信息

```yml
oauth:
  #token有效期24小时
  access-token-validity-seconds: 86400
  #refresh token有效期48小时
  refresh-token-validity-seconds: 172800
  client:
    #普通用户
    common: 
      clinet-id: oauth-client-id
      client-secret: oauth-client-secret
    #微信小程序用户
    weixin:
      clinet-id: wx-oauth-client-id
      client-secret: wx-oauth-client-secret
  #微信登陆配置    
  weixin:
    small-program: 
      #小程序登陆接口
      auth-url: https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code&appid=${oauth.weixin.small-program.appid}&secret=${oauth.weixin.small-program.secret}&js_code=
      #配置放在了nacos配置中心
      #appid: 
      #secret:     
  #下面的请求路径会跳过权限校验 
  path-matchers:
    permit-all: '/test**, /test/**, /free/**, /static/**, /register**, /health, /info, /hystrix.stream/**, /trace, /features, /dump, /webjars/**, /swagger-ui.html, /swagger-resources/**, /**/v2/api-docs, /v2/api-docs, /favicon.ico, /actuator/**'
  jks:
    #私钥配置
    path: certs/auth-jwt.jks
    #私钥密码
    password: 123456
    #alias
    key-pair-alias: auth-jwt 
```

## 微信小程序登陆
微信小程序登陆接口调用和普通登陆类似，需要将js_code作为用户名参数，不需要password参数，后台将调用微信小程序接口查询openid，根据openid查询用户信息，如果用户不存在则新增一个小程序用户

```
http://localhost:8080/oauth/token?client_id=wx-oauth-client-id&client_secret=wx-oauth-client-secret&grant_type=password&username=12345678
```

## 未实现的功能
* 代码中未实现数据库查询功能，需要自己实现
* get请求调用微信api未实现，需要自己实现

## WebSecurityConfig配置中重写了AuthenticationManager相关

* 普通用户按照原有方式处理
* 小程序用户根据js_code调用微信api查询是否存在openid，如果获取到openid则根据openid生成用户（如果用户不存在）

## 用户表结构

表结构可参见SysUser类

## 认证流程

<a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">OAuth2官方文档</a>

```
  +--------+                                           +---------------+
  |        |--(A)------- Authorization Grant --------->|               |
  |        |                                           |               |
  |        |<-(B)----------- Access Token -------------|               |
  |        |               & Refresh Token             |               |
  |        |                                           |               |
  |        |                            +----------+   |               |
  |        |--(C)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(D)- Protected Resource --| Resource |   | Authorization |
  | Client |                            |  Server  |   |     Server    |
  |        |--(E)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(F)- Invalid Token Error -|          |   |               |
  |        |                            +----------+   |               |
  |        |                                           |               |
  |        |--(G)----------- Refresh Token ----------->|               |
  |        |                                           |               |
  |        |<-(H)----------- Access Token -------------|               |
  +--------+           & Optional Refresh Token        +---------------+

               Figure: Refreshing an Expired Access Token

    图所示的流程包括以下步骤：

   （A）客户端通过向客户端进行身份验证来请求访问令牌
        授权服务器并显示授权授权。

   （B）授权服务器对客户端进行身份验证并验证
        授权授予，如果有效，则颁发访问令牌
        和刷新令牌。

   （C）客户端向资源发出受保护的资源请求
        通过显示访问令牌来访问服务器。

   （D）资源服务器验证访问令牌，如果有效，
        服务请求。

   （E）重复步骤（C）和（D），直到访问令牌过期。如果
        客户端知道访问令牌已过期，则跳至步骤（G）；
        否则，它将发出另一个受保护的资源请求。

   （F）由于访问令牌无效，因此资源服务器返回
        无效的令牌错误。          
             
  （G）客户端通过与进行身份验证来请求新的访问令牌
        授权服务器并显示刷新令牌。的
        客户端身份验证要求基于客户端类型
        和授权服务器策略上。

   （H）授权服务器对客户端进行身份验证并验证
        刷新令牌，如果有效，则发出新的访问令牌（并且，
        （可选）新的刷新令牌）。
```
      
            