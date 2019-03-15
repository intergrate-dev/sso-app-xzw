DELETE FROM `admins`;
INSERT INTO `admins` (`id`, `loginname`, `realname`, `password`, `slat`, `email`, `mobile`, `enabled`, `roles`, `permissions`) VALUES
    (1, 'sa', '超级管理员', '8130c97775b44e5605dab545d4a375e7f32c2f29', '123456', 'wzxgcpxx@founder.com', '00000000000', 'yes', 'super_admin', ','),
    (2, 'zmc', 'chaochao', '8130c97775b44e5605dab545d4a375e7f32c2f29', '123456', 'wzxgcpxx@founder.com', '00000000000', 'yes', 'user_admin,account_admin,log_admin', ',');

DELETE FROM `default_oauth_remote_user`;

DELETE FROM `users`;
INSERT INTO `users` VALUES ('2', 'APP2', '15538083808', null, '1', '2017-12-14 15:54:51', 'APP2', '/static/images/face/default_avatarSmall.jpg', '/static/images/face/default_avatarMiddle.jpg', '/static/images/face/default_avatarLarge.jpg', '2', null, null, '141');


DELETE FROM `local_principal`;
INSERT INTO `local_principal` VALUES ('2', '2', 'APP2', '15538083808', null, 'e9594bf3af70baccca8bc6a0395fc6c6fb4f1f08', 'c217c84541b9f7d8');


DELETE FROM `oauth_client_config`;
INSERT INTO `oauth_client_config` (`id`, `provider_name`, `provider`, `oauth_version`, `description`, `app_id`, `secret_key`, `redirect_url`, `enabled`) VALUES
    (1, '新浪微博', 'sina_weibo', '2', NULL, '1173075441', 'a0da0dbaeb9f8e0b381f18a81f4887c9', NULL, '1');

DELETE FROM `user_oauth_binding`;
