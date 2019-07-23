### 认证服务：利用JWT实现无状态用户权限及服务权限认证
 
 - [login](#login)
 - [verify](#verify)
 
#### login(登录授权)

 1. 先校验传递过来的username, password是否正确
 2. 使用rsa非对称加密算法，将第一步查询出的uid和username用私钥加密生成token
 3. 将生成的token存入用户的cookie中，下次请求时直接通过cookie中携带的token进行身份认证

```http request
POST /login
```

```java
@PostMapping("login")
public ResponseEntity<Void> login(String username, String password, HttpServletRequest request, HttpServletResponse response)
```

#### verify(解析token中的用户信息)

 1. 利用公钥解析用户请求中的token信息
 2. 若token有效（正确且未失效），则利用私钥重新生成token，然后写入cookie中
 3. 返回解析的用户信息

```http request
GET /verify
```

```java
@GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response)
```