### 用户服务：主要用来进行用户注册、登录、查询等操作。
 
 - [check/data/type](#check/data/type)
 - [code](#code)
 - [register](#register)
 - [query](#query)
 
#### check/data/type(检查用户名或手机号是否存在)

```http request
GET /check/data/type
```

```java
@GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable("data") String data, @PathVariable("type") Integer type)
```

#### code(向指定手机号发送短信，用于用户注册)

 1. 先随机生成6位数字
 2. 利用mq异步调用短信服务，向指定手机号发送验证码
 3. 将手机号作为key，验证码作为value 存入redis，并设置过期时间5min

```http request
POST /code
```

```java
@PostMapping("code")
public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone)
```

#### register(填写验证码后的注册操作)

 1. 比较传入的code和redis中的code是否相同
 2. 生成一个盐值，用于对密码加密
 3. 将用户信息，盐值和密码存入mysql数据库中

```http request
POST /register
```

```java
@PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult bindingResult, String code)
```

#### query(根据用户名和密码查询用户信息)

```http request
GET /query
```

```java
@GetMapping("/query")
    public ResponseEntity<User> queryUserByUsernameAndPassword(String username, String password)
```
