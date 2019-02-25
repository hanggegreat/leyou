## 乐优商城项目
### 1.1 简介
乐优商城是一个全品类的电商购物网站(B2C模式)，采用目前流行的微服务架构方案设计
乐优商城选择了以SpringCloud为核心的，基于Rest风格的微服务架构
### 1.2 系统架构

![image](https://raw.githubusercontent.com/hanggegreat/leyou/master/imgs/architecture.png)

 - 整个系统采用了前后端分离的开发模式
 - 前端基于Vue相关技术栈进行开发，并通过ajax与后端服务进行交互
 - 前端通过nginx部署，并利用nginx实现对后台服务的反向代理和负载均衡
 - 后端采用SpringCloud技术栈来搭建微服务集群，并对外提供Rest风格接口
 - Zuul作为整个微服务入口，实现请求路由、负载均衡、限流、权限控制等功能
 
**后端技术栈：**
 - Spring Boot 2.x
 - Mybatis 3.x
 - Spring Cloud Greenwich
 - Redis 5.x
 - RabbitMQ 3.x
 - Elasticsearch 6.x
 - nginx 1.14.1
 - FastDFS 5.x
 - thymeleaf 3.x
 - JWT

[商品微服务：ly-item](https://github.com/hanggegreat/leyou/tree/master/ly-item)

 ##### 商品微服务：商品及商品分类、品牌、库存等的服务
  - 商品分类管理
  - 商品品牌管理
  - 商品规格参数管理：因为规格的可变性，采用了竖表设计，分为规格和规格组表
  - 商品管理：难点是spu和sku的设计，以及sku的动态属性
  - 库存管理：库存加减采用乐观锁方案
  
[搜索微服务：ly-search](https://github.com/hanggegreat/leyou/tree/master/ly-search)

##### 搜索微服务：实现搜索功能
 - 采用elasticsearch实现商品的全文检索功能
 - 难点是搜索的过滤条件生成

[订单微服务：ly-order](https://github.com/hanggegreat/leyou/tree/master/ly-order)  
##### 订单微服务：实现订单相关服务，完成支付平台对接
 - 订单的表设计，状态记录
 - 创建订单需要同时减少库存，跨服务业务，需要注意事务处理
    - 查询订单提交的商品信息
    - 计算订单总价格
    - 写入订单、订单详情、订单状态
    - 减少库存，远程同步调用商品微服务，实现库存减少（若采用异步减库存，可能需要引入分布式事务）
 - 对接微信支付

[购物车微服务：ly-cart](https://github.com/hanggegreat/leyou/tree/master/ly-cart)  
##### 购物车服务：实现购物车相关服务
 - 离线购物车: 主要使用localstorage保存到客户端，几乎不与服务端交互
 - 在线购物车: 使用redis实现

[用户微服务: ly-user](https://github.com/hanggegreat/leyou/tree/master/ly-user)
##### 用户微服务：用户的登录注册、用户信息管理等业务
 - 用户注册
 - 注册数据校验
 - 查询用户信息
 - 收货地址管理

[认证微服务：ly-auth](https://github.com/hanggegreat/leyou/tree/master/ly-auth)
##### 认证服务：用户权限及服务权限认证
 - 权限管理
 - 登录token生成
 - 登录token认证
 - 服务间token生成
 - 服务间token认证

[短信微服务：ly-sms](https://github.com/hanggegreat/leyou/tree/master/ly-order)
##### 短信微服务：完成短信的发送
 - 对接腾讯云平台，通过RabbitMQ实现异步的短信发送  

[文件上传微服务：ly-upload](https://github.com/hanggegreat/leyou/tree/master/ly-upload)  
##### 实现静态文件上传功能
 - 利用fastDFS分布式文件系统，将文件存储到nginx服务器上

[公共依赖模块：ly-common](https://github.com/hanggegreat/leyou/tree/master/ly-common)  
##### 为所有微服务提供公共依赖

[网关模块：ly-gateway](https://github.com/hanggegreat/leyou/tree/master/ly-gateway)  
##### Zuul作为整个微服务入口，实现请求路由、负载均衡、限流、权限控制等功能

[静态页面模块：ly-page](https://github.com/hanggegreat/leyou/tree/master/ly-page)  
##### 将服务器端渲染生成的thymeleaf视图保存到服务器上，通过nginx可以实现访问静态页面，避免服务端渲染
 - 在页面内容发生改变时，通过RabbitMQ实现异步更新静态页面

[eureka：ly-registry](https://github.com/hanggegreat/leyou/tree/master/ly-registry)  
##### eureka注册中心，所有微服务都要注册到eureka服务
