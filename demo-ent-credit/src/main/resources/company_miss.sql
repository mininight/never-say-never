/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : yzpt_below

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 04/08/2024 16:12:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for company_miss
-- ----------------------------
DROP TABLE IF EXISTS `company_miss`;
CREATE TABLE `company_miss`  (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `new_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `comp_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of company_miss
-- ----------------------------
INSERT INTO `company_miss` VALUES ('东营众聚企业信息咨询有限公司', NULL, '27009362923518');
INSERT INTO `company_miss` VALUES ('临沂市罗庄区久源民间融资登记服务有限公司', NULL, '21276962068401');
INSERT INTO `company_miss` VALUES ('博兴晟世融通企业咨询有限公司', '山东省博兴县晟世融通企业咨询有限公司', '20806628140274');
INSERT INTO `company_miss` VALUES ('博山舜兴民间融资登记服务有限公司', '淄博舜兴中小企业服务有限公司', '21355257429417');
INSERT INTO `company_miss` VALUES ('安丘市众聚企业管理咨询有限公司', NULL, '21599123211522');
INSERT INTO `company_miss` VALUES ('寿光腾聚企业管理咨询有限公司', NULL, '20979041440319');
INSERT INTO `company_miss` VALUES ('山东东进企业信息咨询有限公司', '山东东进企业管理咨询有限公司', '87475912452452');
INSERT INTO `company_miss` VALUES ('山东佰沃资产管理有限公司', NULL, '89397530687262');
INSERT INTO `company_miss` VALUES ('山东腾聚企业信息咨询有限公司', NULL, '21600953422897');
INSERT INTO `company_miss` VALUES ('山东苾城企业管理咨询有限公司', '山东苾诚企业管理咨询有限公司', '20761231228731');
INSERT INTO `company_miss` VALUES ('山东蒙成企业信息咨询有限公司', '山东蒙成企业管理咨询有限公司', '21429157920127');
INSERT INTO `company_miss` VALUES ('广饶县国腾商务信息咨询有限公司', NULL, '26797082829429');
INSERT INTO `company_miss` VALUES ('广饶县聚成商务信息咨询有限公司', NULL, '75726001373276');
INSERT INTO `company_miss` VALUES ('惠民县腾云企业管理咨询有限公司', NULL, '20953710892281');
INSERT INTO `company_miss` VALUES ('招远万众企业信息咨询有限公司', NULL, '27809552772018');
INSERT INTO `company_miss` VALUES ('无棣龙聚企业信息咨询有限公司', NULL, '20911582135420');
INSERT INTO `company_miss` VALUES ('日照鑫诚民间融资登记服务有限公司', NULL, '27963801053131');
INSERT INTO `company_miss` VALUES ('枣庄智享企业管理咨询有限公司', NULL, '20963871068621');
INSERT INTO `company_miss` VALUES ('枣庄滕聚企业管理咨询有限公司', NULL, '20979702145629');
INSERT INTO `company_miss` VALUES ('栖霞瑞诚企业管理咨询有限公司', NULL, '30821592593130');
INSERT INTO `company_miss` VALUES ('泰安亿丰商务信息咨询有限公司', NULL, '28784338857132');
INSERT INTO `company_miss` VALUES ('济南云腾企业管理咨询有限公司', NULL, '21030632347812');
INSERT INTO `company_miss` VALUES ('济南金澍民间融资登记服务有限公司', NULL, '21631282386132');
INSERT INTO `company_miss` VALUES ('济南龙聚民间资本管理有限公司', NULL, '28809969271193');
INSERT INTO `company_miss` VALUES ('淄博一淼民间资本管理股份有限公司', NULL, '75082166195157');
INSERT INTO `company_miss` VALUES ('淄博亿信企业信息咨询有限公司', NULL, '27809638749118');
INSERT INTO `company_miss` VALUES ('淄博华奕企业管理咨询有限公司', NULL, '20933585021382');
INSERT INTO `company_miss` VALUES ('淄博拓达企业信息咨询有限公司', '淄博拓达企业管理咨询有限公司', '20973324239836');
INSERT INTO `company_miss` VALUES ('淄博明诚企业管理咨询有限公司', NULL, '20914271835610');
INSERT INTO `company_miss` VALUES ('淄博晟腾民间融资登记服务有限公司', NULL, '28613986702246');
INSERT INTO `company_miss` VALUES ('淄博环博信息咨询有限公司', NULL, '10695356052744');
INSERT INTO `company_miss` VALUES ('淄博环洲企业信息咨询有限公司', NULL, '29262313514169');
INSERT INTO `company_miss` VALUES ('淄博环通企业信息咨询有限公司', NULL, '20916219810114');
INSERT INTO `company_miss` VALUES ('淄博瑞创商务信息咨询有限公司', NULL, '20738250294913');
INSERT INTO `company_miss` VALUES ('淄博腾硕企业信息咨询有限公司', NULL, '20933249375774');
INSERT INTO `company_miss` VALUES ('滨州云聚企业管理咨询有限责任公司', NULL, '20793217912714');
INSERT INTO `company_miss` VALUES ('滨州云聚企业管理咨询有限责任公司众鑫分公司', NULL, '20797672727829');
INSERT INTO `company_miss` VALUES ('滨州市泽昌企业管理咨询有限公司', NULL, '20801082781320');
INSERT INTO `company_miss` VALUES ('滨州瑞通企业信息咨询管理有限公司', NULL, '20986249455827');
INSERT INTO `company_miss` VALUES ('潍坊市博通企业信息咨询有限公司', '潍坊市博通企业管理服务有限公司', '21020191078010');
INSERT INTO `company_miss` VALUES ('潍坊聚富企业信息咨询有限公司', NULL, '20954179127824');
INSERT INTO `company_miss` VALUES ('潍坊聚诚企业信息咨询有限公司', NULL, '27109564306268');
INSERT INTO `company_miss` VALUES ('潍坊聚诚企业信息咨询有限公司坊子分公司', NULL, '21721177909694');
INSERT INTO `company_miss` VALUES ('潍坊聚诚企业信息咨询有限公司新华路分公司', NULL, '20794267992415');
INSERT INTO `company_miss` VALUES ('潍坊腾鑫企业管理咨询服务有限公司', NULL, '20936101105117');
INSERT INTO `company_miss` VALUES ('潍坊金聚企业管理咨询服务有限公司', NULL, '21910483986280');
INSERT INTO `company_miss` VALUES ('烟台腾聚企业信息咨询有限公司', NULL, '20919412292929');
INSERT INTO `company_miss` VALUES ('聊城市腾旭企业信息咨询有限公司', NULL, '20946178205827');
INSERT INTO `company_miss` VALUES ('胶州市聚梦诚企业管理信息咨询有限公司', NULL, '21929217116525');
INSERT INTO `company_miss` VALUES ('莒南云聚企业信息咨询有限公司', '莒南云聚企业信息咨询管理有限公司', '21682632710130');
INSERT INTO `company_miss` VALUES ('莱州腾聚企业管理信息咨询有限公司', NULL, '21023180826177');
INSERT INTO `company_miss` VALUES ('莱芜腾聚企业管理咨询有限公司', NULL, '27509071553418');
INSERT INTO `company_miss` VALUES ('诸城腾聚企业信息咨询有限公司', NULL, '20936304137315');
INSERT INTO `company_miss` VALUES ('邹平县乾聚企业信息咨询有限公司', NULL, '21055183450523');
INSERT INTO `company_miss` VALUES ('青岛博汇企业咨询管理有限公司', NULL, '21551229051237');
INSERT INTO `company_miss` VALUES ('青岛商都企业信息咨询有限公司', NULL, '21711663122731');
INSERT INTO `company_miss` VALUES ('青岛宇沃中小企业服务有限公司', NULL, '98202405046717');
INSERT INTO `company_miss` VALUES ('青岛腾华企业信息咨询有限公司', NULL, '21317961391291');
INSERT INTO `company_miss` VALUES ('青岛腾泽企业信息咨询有限公司', NULL, '21377991301921');
INSERT INTO `company_miss` VALUES ('青岛腾聚企业管理信息咨询有限公司', NULL, '11233959252313');
INSERT INTO `company_miss` VALUES ('龙口众合兴怡民间融资登记服务有限公司', '龙口众合兴怡咨询服务有限公司', '26855269156423');

SET FOREIGN_KEY_CHECKS = 1;
