/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.0.44
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : 192.168.0.44:3306
 Source Schema         : fileserver

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 15/05/2020 14:46:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for filerefrelation
-- ----------------------------
DROP TABLE IF EXISTS `filerefrelation`;
CREATE TABLE `filerefrelation`  (
  `ID` bigint(20) NOT NULL,
  `GMT_CREATE` datetime(0) NULL DEFAULT NULL,
  `GMT_MODIFIED` datetime(0) NULL DEFAULT NULL,
  `STATUS` int(11) NULL DEFAULT NULL,
  `FILEREF_ID` bigint(20) NOT NULL,
  `APP_ID` int(11) NOT NULL,
  `REMARK` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `INDEX_FILE_APP`(`FILEREF_ID`, `APP_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for yozofileref
-- ----------------------------
DROP TABLE IF EXISTS `yozofileref`;
CREATE TABLE `yozofileref`  (
  `ID` bigint(20) NOT NULL,
  `GMT_CREATE` datetime(0) NULL DEFAULT NULL,
  `GMT_MODIFIED` datetime(0) NULL DEFAULT NULL,
  `STATUS` int(11) NULL DEFAULT NULL,
  `FILE_MD5` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `STORAGE_URL` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `FILE_SIZE` bigint(20) NULL DEFAULT NULL,
  `REMARK` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `INDEX_FILE_MD5`(`FILE_MD5`) USING BTREE,
  UNIQUE INDEX `INDEX_STORAGE_URL`(`STORAGE_URL`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
