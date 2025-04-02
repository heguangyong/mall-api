

-- 打印内容配置
ALTER TABLE `eb_merchant_print` ADD COLUMN `content` text NULL COMMENT '打印内容';
-- 商户强制开关
ALTER TABLE `eb_merchant` ADD COLUMN `is_force_shutdown` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户是否强制关闭:0-否，1-强制关闭';
-- 商户入驻短信通知
INSERT INTO `eb_system_notification` (`id`, `mark`, `type`, `description`, `is_wechat`, `wechat_id`, `is_routine`, `routine_id`, `is_sms`, `sms_id`, `send_type`, `create_time`) VALUES (12, 'merchantSettledApply', '商户入驻申请提醒', '商户入驻申请提醒', 0, 0, 0, 0, 1, 12, 2, '2025-02-21 15:57:22');
INSERT INTO `eb_sms_template` (`id`, `temp_id`, `temp_type`, `title`, `type`, `status`, `content`, `create_time`, `update_time`) VALUES (12, '1019578768', 1, '商户入驻提醒', '通知', 1, '【CRMEB】您好，有新商户{$mer_name}申请入驻，请到总后台商户管理下商户申请入驻查看', '2025-02-21 15:58:10', '2025-02-21 15:58:37');
-- 权限部分
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1027, 1034, '获取秒杀样式', '', 'platform:system:config:seckill:style:get', '', 'A', 0, 1, 0, 3, '2025-02-17 15:57:36', '2025-02-18 15:11:34');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1028, 1034, '保存秒杀样式', '', 'platform:system:config:seckill:style:save', '', 'A', 0, 1, 0, 3, '2025-02-17 15:58:14', '2025-02-18 15:11:46');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1029, 48, '用户余额记录', '', 'platform:user:balance:record', '', 'A', 0, 1, 0, 3, '2025-02-17 15:58:51', '2025-02-17 15:58:51');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1030, 48, '用户积分记录', '', 'platform:user:integral:record', '', 'A', 0, 1, 0, 3, '2025-02-17 15:59:03', '2025-02-17 15:59:03');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1031, 48, '用户佣金记录', '', 'platform:user:brokerage:record', '', 'A', 0, 1, 0, 3, '2025-02-17 15:59:16', '2025-02-17 15:59:16');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1032, 48, '用户经验记录', '', 'platform:user:experience:record', '', 'A', 0, 1, 0, 3, '2025-02-17 15:59:29', '2025-02-17 15:59:29');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1033, 48, '用户签到记录', '', 'platform:user:sign:record', '', 'A', 0, 1, 0, 3, '2025-02-17 15:59:41', '2025-02-17 15:59:41');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1034, 598, '样式配置', '', '', '/marketing/seckill/styleConfig', 'C', 0, 1, 0, 3, '2025-02-18 15:10:31', '2025-02-18 15:11:11');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1035, 817, '获取打印内容配置', '', 'merchant:print:get:content', '', 'A', 1, 1, 0, 4, '2025-02-19 15:49:54', '2025-02-19 15:49:54');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1036, 817, '保存打印内容配置', '', 'merchant:print:save:content', '', 'A', 1, 1, 0, 4, '2025-02-19 15:50:09', '2025-02-19 15:50:09');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1037, 176, '打印配置', '', '', '/operation/printing', 'C', 1, 1, 0, 4, '2025-02-20 18:11:20', '2025-02-21 10:19:13');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1038, 1037, '电子面单', '', '', '/operation/printing/electrsheet', 'C', 1, 1, 0, 4, '2025-02-20 18:19:36', '2025-02-21 10:01:40');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1039, 20, '后台管理员短信开关', '', 'platform:admin:update:receive:sms', '', 'A', 0, 1, 0, 3, '2025-02-22 15:19:38', '2025-02-22 15:19:38');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1040, 919, '物流信息维护-月结账号配置', '', 'merchant:express:update', '', 'A', 1, 1, 0, 4, '2025-02-24 14:45:37', '2025-02-24 14:45:37');

INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1027);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1028);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1029);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1030);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1031);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1032);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1033);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1034);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1039);

INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1035);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1036);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1037);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1038);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1040);

-- 转移菜单层级 确认自己新增后的菜单id，小票打印转移到打印配置之下
update eb_system_menu m set m.pid = 1037, m.component = '/operation/printing/printreceipt' where m.`name`='小票打印' and m.id = 816;
update eb_system_menu m set m.pid = 816 where m.pid = 817;
update eb_system_menu m set m.is_delte = 1 where m.id = 817;


-- 去除权限
DELETE FROM eb_system_menu WHERE id = 72;
DELETE FROM eb_system_menu WHERE id = 591;
DELETE FROM eb_system_role_menu WHERE menu_id = 72;
DELETE FROM eb_system_role_menu WHERE menu_id = 591;

-- 发货单表增加发货记录类型
ALTER TABLE `eb_order_invoice` ADD COLUMN `express_record_type` int(2) DEFAULT '1' COMMENT '发货记录类型，1快递发货、2电子面单';
-- 商户表增加电子面单开关
ALTER TABLE `eb_merchant` ADD COLUMN `electr_printing_switch` int(2) DEFAULT '0' COMMENT '电子面单开关：0关闭，1=开启';
-- 商户电子面单配置表
-- ----------------------------
-- Table structure for eb_merchant_elect
-- ----------------------------
DROP TABLE IF EXISTS `eb_merchant_elect`;
CREATE TABLE `eb_merchant_elect` (
  `op` int(11) DEFAULT NULL COMMENT '打印机类型0通用1快递100',
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mer_id` int(11) DEFAULT NULL COMMENT '商户ID',
  `cloud_print_no` varchar(255) DEFAULT NULL COMMENT '云打印机编号',
  `sender_addr` varchar(255) DEFAULT NULL COMMENT '寄件地址',
  `sender_username` varchar(255) DEFAULT NULL COMMENT '寄件人',
  `sender_phone` varchar(11) DEFAULT NULL COMMENT '寄件人电话',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商户电子面单配置表';

INSERT INTO `eb_system_config` (`id`, `name`, `title`, `form_id`, `value`, `status`, `create_time`, `update_time`, `form_name`) VALUES (null, 'seckill_style_config', '', 0, '1', 0, '2025-03-05 14:13:22', '2025-03-05 14:13:22', '');


