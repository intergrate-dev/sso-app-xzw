DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `loginname` char(255) NOT NULL COMMENT '管理员用户名',
  `realname` char(255) NOT NULL COMMENT '管理员真实名称',
  `password` char(255) NOT NULL COMMENT '管理员密码',
  `slat` char(255) NOT NULL COMMENT '管理员密码盐值',
  `email` char(255) NOT NULL COMMENT '管理员邮箱用于事件通知',
  `mobile` char(255) NOT NULL COMMENT '管理员手机号用于紧急事件短信通知',
  `enabled` char(10) NOT NULL COMMENT '管理员的激活状态',
  `roles` varchar(255) DEFAULT NULL COMMENT '管理员角色',
  `permissions` varchar(255) DEFAULT NULL COMMENT '管理员权限',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginname` (`loginname`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='管理员用户表';

DROP TABLE IF EXISTS `default_oauth_remote_user`;
CREATE TABLE `default_oauth_remote_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(200) NOT NULL COMMENT '用户在Oauth端的UID',
  `username` varchar(200) NOT NULL COMMENT '用户名',
  `nickname` varchar(200) NOT NULL COMMENT '用户昵称',
  `avatar_small` varchar(200) NOT NULL COMMENT '用户的小头像',
  `avatar_middle` varchar(200) NOT NULL COMMENT '用户的中头像',
  `avatar_large` varchar(200) NOT NULL COMMENT '用户的大头像',
  `access_token` varchar(200) NOT NULL COMMENT 'AccessToken',
  `token_expires_time` datetime NOT NULL COMMENT 'AccessToken过期时间',
  `provider` varchar(200) NOT NULL COMMENT '供应商',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8 COMMENT='默认的远程用户表';

DROP TABLE IF EXISTS `oauth_client_config`;
CREATE TABLE `oauth_client_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `provider_name` varchar(50) DEFAULT NULL COMMENT 'Oauth服务提供商显示名称',
  `provider` varchar(50) NOT NULL COMMENT 'Oauth服务提供商标示',
  `oauth_version` varchar(50) NOT NULL DEFAULT '2' COMMENT 'Oauth服务版本',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `app_id` varchar(200) DEFAULT NULL COMMENT '连接Oauth的APPID',
  `secret_key` varchar(200) DEFAULT NULL COMMENT '连接Oauth的SecretKey',
  `redirect_url` varchar(200) DEFAULT NULL COMMENT 'Oauth回调地址',
  `enabled` varchar(200) NOT NULL COMMENT '是否已经启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `provider` (`provider`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='Oauth客户端的配置信息';



DROP TABLE IF EXISTS `local_principal`;
CREATE TABLE `local_principal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '对应的用户ID',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名 可用于登录',
  `phone` varchar(100) DEFAULT NULL COMMENT '用户用于登录的手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '用户用于登录的邮箱',
  `password` varchar(100) DEFAULT NULL COMMENT '此条信息的密码',
  `salt` varchar(100) DEFAULT NULL COMMENT '此条信息的盐值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  CONSTRAINT `userid` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8 COMMENT='本地认证信息凭证表 包含用户本地账户的登录';

DROP TABLE IF EXISTS `user_oauth_binding`;
CREATE TABLE `user_oauth_binding` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户表ID',
  `provider` varchar(200) NOT NULL COMMENT 'Oauth供应商',
  `oauth_uid` varchar(200) DEFAULT NULL COMMENT '用户在Oauth端的UID',
  `bind_time` datetime NOT NULL,
  `nickname` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bind_uid_provider` (`user_id`,`provider`) USING BTREE COMMENT '一个本地用户每个渠道只能绑定一个',
  UNIQUE KEY `oauth` (`provider`,`oauth_uid`) USING BTREE COMMENT '一个微博账号只能绑定一个本地账户',
  CONSTRAINT `FK_60prs29i23rfqf7v7dlr5cppk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8 COMMENT='Oauth用户信息和本地用户信息的绑定表';
