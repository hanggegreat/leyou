# 商品的品牌信息，一个品牌下有多个商品（spu），一对多关系
CREATE TABLE `tb_brand`
(
    `id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '品牌id',
    `name`   varchar(32) NOT NULL COMMENT '品牌名称',
    `image`  varchar(128) DEFAULT '' COMMENT '品牌图片地址',
    `letter` char(1)      DEFAULT '' COMMENT '品牌的首字母',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 325400
  DEFAULT CHARSET = utf8 COMMENT ='品牌表，一个品牌下有多个商品（spu），一对多关系';

# 商品的分类信息：一个商品分类下有很多商品、一个商品分类下有很多品牌、一个品牌可能属于不同的分类、一个品牌下也会有很多商品
CREATE TABLE `tb_category`
(
    `id`        bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '类目id',
    `name`      varchar(32) NOT NULL COMMENT '类目名称',
    `parent_id` bigint(20)  NOT NULL COMMENT '父类目id,顶级类目填0',
    `is_parent` tinyint(1)  NOT NULL COMMENT '是否为父节点，0为否，1为是',
    `sort`      int(4)      NOT NULL COMMENT '排序指数，越小越靠前',
    PRIMARY KEY (`id`),
    KEY `key_parent_id` (`parent_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1424
  DEFAULT CHARSET = utf8 COMMENT ='商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系';

# 商品分类和品牌的中间表，两者是多对多关系
CREATE TABLE `tb_category_brand`
(
    `category_id` bigint(20) NOT NULL COMMENT '商品类目id',
    `brand_id`    bigint(20) NOT NULL COMMENT '品牌id',
    PRIMARY KEY (`category_id`, `brand_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='商品分类和品牌的中间表，两者是多对多关系';

# spu表，该表描述的是一个抽象性的商品
CREATE TABLE `tb_spu`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'spu id',
    `title`            varchar(128) NOT NULL DEFAULT '' COMMENT '标题',
    `sub_title`        varchar(256)          DEFAULT '' COMMENT '子标题',
    `cid1`             bigint(20)   NOT NULL COMMENT '1级类目id',
    `cid2`             bigint(20)   NOT NULL COMMENT '2级类目id',
    `cid3`             bigint(20)   NOT NULL COMMENT '3级类目id',
    `brand_id`         bigint(20)   NOT NULL COMMENT '商品所属品牌id',
    `saleable`         tinyint(1)   NOT NULL DEFAULT '1' COMMENT '是否上架，0下架，1上架',
    `valid`            tinyint(1)   NOT NULL DEFAULT '1' COMMENT '是否有效，0已删除，1有效',
    `create_time`      datetime              DEFAULT NULL COMMENT '添加时间',
    `last_update_time` datetime              DEFAULT NULL COMMENT '最后修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 196
  DEFAULT CHARSET = utf8 COMMENT ='spu表，该表描述的是一个抽象性的商品，比如 iphone8';

# spu表中的大字段，做了垂直拆分
CREATE TABLE `tb_spu_detail`
(
    `spu_id`        bigint(20)    NOT NULL,
    `description`   text COMMENT '商品描述信息',
    `generic_spec`  varchar(2048) NOT NULL DEFAULT '' COMMENT '通用规格参数数据',
    `special_spec`  varchar(1024) NOT NULL COMMENT '特有规格参数及可选值信息，json格式',
    `packing_list`  varchar(1024)          DEFAULT '' COMMENT '包装清单',
    `after_service` varchar(1024)          DEFAULT '' COMMENT '售后服务',
    PRIMARY KEY (`spu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# sku表，表示具体的商品实体
# sku特有的参数键值对，自己维护了一个json字段来表示了
CREATE TABLE `tb_sku`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'sku id',
    `spu_id`           bigint(20)   NOT NULL COMMENT 'spu id',
    `title`            varchar(256) NOT NULL COMMENT '商品标题',
    `images`           varchar(1024)         DEFAULT '' COMMENT '商品的图片，多个图片以‘,’分割',
    `price`            bigint(15)   NOT NULL DEFAULT '0' COMMENT '销售价格，单位为分',
    `indexes`          varchar(32)           DEFAULT '' COMMENT '特有规格属性在spu属性模板中的对应下标组合',
    `own_spec`         varchar(1024)         DEFAULT '' COMMENT 'sku的特有规格参数键值对，json格式，反序列化时请使用linkedHashMap，保证有序',
    `enable`           tinyint(1)   NOT NULL DEFAULT '1' COMMENT '是否有效，0无效，1有效',
    `create_time`      datetime     NOT NULL COMMENT '添加时间',
    `last_update_time` datetime     NOT NULL COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    KEY `key_spu_id` (`spu_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 27359021730
  DEFAULT CHARSET = utf8 COMMENT ='sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8';

# 规格参数的分组表(每个类型的商品参数规格相同，并且有多个参数规格组)
CREATE TABLE `tb_spec_group`
(
    `id`   bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cid`  bigint(20)  NOT NULL COMMENT '商品分类id，一个分类下有多个规格组',
    `name` varchar(32) NOT NULL COMMENT '规格组的名称',
    PRIMARY KEY (`id`),
    KEY `key_category` (`cid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 28
  DEFAULT CHARSET = utf8 COMMENT ='规格参数的分组表，每个商品分类下有多个规格参数组';

# 数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0
CREATE TABLE `tb_spec_param`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cid`       bigint(20)   NOT NULL COMMENT '商品分类id',
    `group_id`  bigint(20)   NOT NULL,
    `name`      varchar(256) NOT NULL COMMENT '参数名',
    `numeric`   tinyint(1)   NOT NULL COMMENT '是否是数字类型参数，true或false',
    `unit`      varchar(256)  DEFAULT '' COMMENT '数字类型参数的单位，非数字类型可以为空',
    `generic`   tinyint(1)   NOT NULL COMMENT '是否是sku通用属性，true或false',
    `searching` tinyint(1)   NOT NULL COMMENT '是否用于搜索过滤，true或false',
    `segments`  varchar(1024) DEFAULT '' COMMENT '数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0',
    PRIMARY KEY (`id`),
    KEY `key_group` (`group_id`),
    KEY `key_category` (`cid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8 COMMENT ='规格参数组下的参数名';

# 库存表，代表库存，秒杀库存等信息
CREATE TABLE `tb_stock`
(
    `sku_id`        bigint(20) NOT NULL COMMENT '库存对应的商品sku id',
    `seckill_stock` int(9) DEFAULT '0' COMMENT '可秒杀库存',
    `seckill_total` int(9) DEFAULT '0' COMMENT '秒杀总数量',
    `stock`         int(9)     NOT NULL COMMENT '库存数量',
    PRIMARY KEY (`sku_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='库存表，代表库存，秒杀库存等信息';

# 订单表
CREATE TABLE `tb_order`
(
    `order_id`          bigint(20)                   NOT NULL COMMENT '订单id',
    `total_pay`         bigint(20)                   NOT NULL COMMENT '总金额，单位为分',
    `actual_pay`        bigint(20)                   NOT NULL COMMENT '实付金额。单位:分。如:20007，表示:200元7分',
    `promotion_ids`     varchar(256) COLLATE utf8_bin DEFAULT '',
    `payment_type`      tinyint(1) unsigned zerofill NOT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
    `post_fee`          bigint(20)                   NOT NULL COMMENT '邮费。单位:分。如:20007，表示:200元7分',
    `create_time`       datetime                      DEFAULT NULL COMMENT '订单创建时间',
    `shipping_name`     varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '物流名称',
    `shipping_code`     varchar(20) COLLATE utf8_bin  DEFAULT NULL COMMENT '物流单号',
    `user_id`           varchar(32) COLLATE utf8_bin NOT NULL COMMENT '用户id',
    `buyer_message`     varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '买家留言',
    `buyer_nick`        varchar(32) COLLATE utf8_bin NOT NULL COMMENT '买家昵称',
    `buyer_rate`        tinyint(1)                    DEFAULT NULL COMMENT '买家是否已经评价,0未评价，1已评价',
    `receiver_state`    varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '收获地址（省）',
    `receiver_city`     varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT '收获地址（市）',
    `receiver_district` varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT '收获地址（区/县）',
    `receiver_address`  varchar(256) COLLATE utf8_bin DEFAULT '' COMMENT '收获地址（街道、住址等详细地址）',
    `receiver_mobile`   varchar(11) COLLATE utf8_bin  DEFAULT NULL COMMENT '收货人手机',
    `receiver_zip`      varchar(16) COLLATE utf8_bin  DEFAULT NULL COMMENT '收货人邮编',
    `receiver`          varchar(32) COLLATE utf8_bin  DEFAULT NULL COMMENT '收货人',
    `invoice_type`      int(1)                        DEFAULT '0' COMMENT '发票类型(0无发票1普通发票，2电子发票，3增值税发票)',
    `source_type`       int(1)                        DEFAULT '2' COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
    PRIMARY KEY (`order_id`),
    KEY `create_time` (`create_time`),
    KEY `buyer_nick` (`buyer_nick`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

# 订单详情表
CREATE TABLE `tb_order_detail`
(
    `id`       bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '订单详情id ',
    `order_id` bigint(20)   NOT NULL COMMENT '订单id',
    `sku_id`   bigint(20)   NOT NULL COMMENT 'sku商品id',
    `num`      int(11)      NOT NULL COMMENT '购买数量',
    `title`    varchar(256) NOT NULL COMMENT '商品标题',
    `own_spec` varchar(1024) DEFAULT '' COMMENT '商品动态属性键值集',
    `price`    bigint(20)   NOT NULL COMMENT '价格,单位：分',
    `image`    varchar(128)  DEFAULT '' COMMENT '商品图片',
    PRIMARY KEY (`id`),
    KEY `key_order_id` (`order_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 126
  DEFAULT CHARSET = utf8 COMMENT ='订单详情表';

# 订单状态表
CREATE TABLE `tb_order_status`
(
    `order_id`     bigint(20) NOT NULL COMMENT '订单id',
    `status`       int(1)   DEFAULT NULL COMMENT '状态：1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价',
    `create_time`  datetime DEFAULT NULL COMMENT '订单创建时间',
    `payment_time` datetime DEFAULT NULL COMMENT '付款时间',
    `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
    `end_time`     datetime DEFAULT NULL COMMENT '交易完成时间',
    `close_time`   datetime DEFAULT NULL COMMENT '交易关闭时间',
    `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
    PRIMARY KEY (`order_id`),
    KEY `status` (`status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='订单状态表';

# 用户表：
CREATE TABLE `tb_user`
(
    `id`       bigint(20)  NOT NULL AUTO_INCREMENT,
    `username` varchar(32) NOT NULL COMMENT '用户名',
    `password` varchar(32) NOT NULL COMMENT '密码，加密存储',
    `phone`    varchar(11) DEFAULT NULL COMMENT '注册手机号',
    `created`  datetime    NOT NULL COMMENT '创建时间',
    `salt`     varchar(32) NOT NULL COMMENT '密码加密的salt值',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 31
  DEFAULT CHARSET = utf8 COMMENT ='用户表';