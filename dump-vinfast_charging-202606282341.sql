-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: vinfast_charging
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '5094c7fa-332c-11f1-9643-c6cd060d0bf7:1-527';

--
-- Table structure for table `charging_stations`
--

DROP TABLE IF EXISTS `charging_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `charging_stations` (
  `station_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `address` text NOT NULL,
  `latitude` decimal(10,8) NOT NULL,
  `longitude` decimal(11,8) NOT NULL,
  `opening_hours` varchar(100) DEFAULT '24/7',
  `image_url` text,
  `rating` decimal(2,1) DEFAULT '0.0',
  `total_reviews` int DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`station_id`),
  KEY `idx_location` (`latitude`,`longitude`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charging_stations`
--

LOCK TABLES `charging_stations` WRITE;
/*!40000 ALTER TABLE `charging_stations` DISABLE KEYS */;
INSERT INTO `charging_stations` VALUES (104,'Ba Đình - Bãi đỗ xe Khách sạn La Thành','226 Vạn Phúc, Liễu Giai, Ba Đình, Hà Nội',21.03350000,105.81420000,'24/7','station-20260628210127-ca2b76c7-53e8-4be0-a47e-249efaba0cf7.jpg',4.3,3,1,'2026-06-28 19:49:25'),(105,'Ba Đình - Vincom Center Metropolis','Hầm B3, 29 Liễu Giai, Ba Đình, Hà Nội',21.03190000,105.81280000,'24/7','station-20260628210315-86dcf3f7-c03e-4c27-8542-05b6d18cf0fa.jpg',5.0,3,1,'2026-06-28 19:49:25'),(107,'Ba Đình - Vinhomes Metropolis (Ô tô B3)','Hầm B3, 29 Liễu Giai, Ba Đình, Hà Nội',21.03100000,105.81300000,'24/7','station-20260628210236-97a5f380-8424-420c-b14a-ebb6737ca684.jpg',4.3,3,1,'2026-06-28 19:49:25'),(108,'Ba Đình - Vinhomes Metropolis (Ô tô B4)','Hầm B4, 29 Liễu Giai, Ba Đình, Hà Nội',21.03050000,105.81200000,'24/7','station-20260628210327-363646a6-6203-46a1-ae3a-1a129dda485f.jpg',4.0,3,1,'2026-06-28 19:49:25'),(109,'Cầu Giấy - Big C Thăng Long','222 Trần Duy Hưng, Trung Hòa, Cầu Giấy, Hà Nội',21.00722720,105.79095260,'24/7','station-20260628210346-63470737-0a3c-4bf1-9fb5-e4ea8d2c194b.jpg',4.5,2,1,'2026-06-28 19:49:25'),(110,'Cầu Giấy - Discovery Complex','Hầm B3, 302 Cầu Giấy, Dịch Vọng, Cầu Giấy, Hà Nội',21.03460000,105.79460000,'24/7','station-20260628210630-21fb26e8-17ca-4f8b-bf4f-5c2c573063c4.png',4.0,1,1,'2026-06-28 19:49:25'),(111,'Hà Đông - HPC Landmark 105','Hầm B2, Văn Khê, La Khê, Hà Đông, Hà Nội',20.97040000,105.76020000,'24/7','station-20260628210644-f15b7c2e-cc55-41b8-ab68-dbba3473bd78.png',4.5,2,1,'2026-06-28 19:49:25'),(112,'Hà Đông - Bình Vượng Tower','200 Quang Trung, Quang Trung, Hà Đông, Hà Nội',20.96780000,105.77250000,'24/7','station-20260628210656-84e2af08-8b1f-4348-af63-4d619b8e2058.jpg',5.0,3,1,'2026-06-28 19:49:25'),(113,'Hà Đông - Vinaconex 21','Ngõ 804 Quang Trung, Phú Lãm, Hà Đông, Hà Nội',20.95750000,105.76100000,'24/7','station-20260628210713-15f10af8-daec-4bd9-95a5-066914d8c532.png',5.0,2,1,'2026-06-28 19:49:25'),(114,'Hà Đông - Dương Nội HH2','Hầm B2, KĐT Dương Nội, Hà Đông, Hà Nội',20.96910000,105.75160000,'24/7','station-20260628210727-1f0cbb53-3a14-416b-97e4-80ccc1f1329f.jpg',4.0,1,1,'2026-06-28 19:49:25'),(115,'Hà Đông - Rainbow Văn Quán','Đường 19/5, Văn Quán, Hà Đông, Hà Nội',20.97810000,105.78770000,'24/7','station-20260628210837-798bfcd6-e5f5-4845-9f60-c8c1275d181e.jpg',4.5,2,1,'2026-06-28 19:49:25'),(116,'Tây Hồ - Somerset West Point','2 Tây Hồ, Quảng An, Tây Hồ, Hà Nội',21.06280000,105.82860000,'24/7','station-20260628210852-d4f488b4-a9f6-490c-b7f8-fc5688f583f7.webp',4.5,2,1,'2026-06-28 19:49:25'),(117,'Tây Hồ - Somerset West Lake','254 Thụy Khuê, Thụy Khuê, Tây Hồ, Hà Nội',21.04350000,105.81900000,'24/7','station-20260628210905-bcb7607b-6b01-4ebf-a66c-02d62829cf0d.webp',5.0,3,1,'2026-06-28 19:49:25'),(118,'Tây Hồ - D’ El Dorado 1','659A Lạc Long Quân, Xuân La, Tây Hồ, Hà Nội',21.07430000,105.81180000,'24/7','station-20260628211020-ea56a121-2e1f-4946-86c1-5e2d9fd4fd59.jpg',4.0,1,1,'2026-06-28 19:49:25'),(119,'Tây Hồ - D’ Le Roi Soleil','59 Xuân Diệu, Quảng An, Tây Hồ, Hà Nội',21.06410000,105.82600000,'24/7','station-20260628211011-f0e4b5d2-2e45-4d74-ae0b-e8ffb2a308a6.jpg',4.5,2,1,'2026-06-28 19:49:25'),(120,'Long Biên - Vinhomes Symphony','Chu Huy Mân, Phúc Đồng, Long Biên, Hà Nội',21.03750000,105.90800000,'24/7','station-20260628211034-e480b943-6370-424b-b6e3-271cba15bbb7.webp',4.7,3,1,'2026-06-28 19:49:25'),(121,'Long Biên - Ruby City 3','Hầm B3, Phúc Lợi, Long Biên, Hà Nội',21.04250000,105.92800000,'24/7','station-20260628211047-31c8fe76-2bf6-4f3c-8d15-651c99bad291.jpg',4.0,2,1,'2026-06-28 19:49:25'),(122,'Long Biên - Đại lý VinFast Long Biên','1 Nguyễn Văn Linh, Gia Thụy, Long Biên, Hà Nội',21.04830000,105.88120000,'24/7','station-20260628211112-d9d3aaa8-11ad-4f32-820e-6a16ab918caa.webp',4.0,1,1,'2026-06-28 19:49:25'),(123,'Gia Lâm - Vincom Ocean Park','KĐT Vinhomes Ocean Park, Đa Tốn, Gia Lâm, Hà Nội',20.99020000,105.94600000,'24/7','station-20260628211201-81c734ec-f008-46c9-b506-66eef49b54d8.jpg',4.0,1,1,'2026-06-28 19:49:25'),(124,'Gia Lâm - Trạm sạc S2.01 Ocean Park','Tòa S2.01, Vinhomes Ocean Park, Gia Lâm, Hà Nội',20.98560000,105.94220000,'24/7','station-20260628211225-066b15a6-ce1d-48f1-b4dd-7276bc11ce29.webp',4.5,2,1,'2026-06-28 19:49:25'),(125,'Gia Lâm - Trạm sạc S2.03 Ocean Park','Tòa S2.03, Vinhomes Ocean Park, Gia Lâm, Hà Nội',20.98450000,105.94150000,'24/7','station-20260628211242-f3fc955d-4890-4037-b95c-2cb3468617c2.webp',4.0,1,1,'2026-06-28 19:49:25'),(126,'Gia Lâm - Trạm sạc TP11 Ocean Park','Tòa TP11, Vinhomes Ocean Park, Gia Lâm, Hà Nội',20.98800000,105.94400000,'24/7','station-20260628211255-557aab3e-64c1-4fa0-b5b1-9a2ca5f1372c.jpeg',4.3,3,1,'2026-06-28 19:49:25'),(127,'Gia Lâm - Trạm sạc Ruby CT01 Ocean Park','Phân khu Ruby, Vinhomes Ocean Park, Gia Lâm, Hà Nội',20.98650000,105.94800000,'24/7','station-20260628211307-1682af1c-4eed-4323-a7a8-d4d7b7098062.webp',4.0,3,1,'2026-06-28 19:49:25'),(128,'Hoàng Mai - Eco Lake View HH1','32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội',20.97340000,105.83950000,'24/7','station-20260628211504-3c39615a-e76f-49d4-a7c0-9cb0c635217c.jpg',5.0,1,1,'2026-06-28 19:49:25'),(129,'Hoàng Mai - Eco Lake View HH2','32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội',20.97300000,105.83900000,'24/7','station-20260628211539-9a2f6a1a-3c08-4d45-99e8-4b1c928f531a.jpg',4.7,3,1,'2026-06-28 19:49:25'),(130,'Hoàng Mai - Eco Lake View HH3','32 Đại Từ, Đại Kim, Hoàng Mai, Hà Nội',20.97250000,105.83850000,'24/7','station-20260628211554-04c4e0c9-e410-4be4-8bdc-6d536d557a40.jpg',4.0,1,1,'2026-06-28 19:49:25'),(131,'Hoàng Mai - Gamuda Gardens','QL1A, Trần Phú, Hoàng Mai, Hà Nội',20.97010000,105.86700000,'24/7','station-20260628211608-366caa9c-6190-4490-b0bc-3ebd6916dafe.jpg',5.0,2,1,'2026-06-28 19:49:25'),(132,'Đống Đa - Vincom Nguyễn Chí Thanh','54 Nguyễn Chí Thanh, Láng Thượng, Đống Đa, Hà Nội',21.02230000,105.81050000,'24/7','station-20260628211622-58ffae09-a724-4edc-9269-5340eba4e435.jpg',4.0,2,1,'2026-06-28 19:49:25'),(133,'Đống Đa - Vincom Phạm Ngọc Thạch','2 Phạm Ngọc Thạch, Kim Liên, Đống Đa, Hà Nội',21.00890000,105.83270000,'24/7','dong_da_vincom_pham_ngoc_thach.jpg',5.0,1,1,'2026-06-28 19:49:25'),(134,'Hai Bà Trưng - Vinhomes Times City','458 Minh Khai, Vĩnh Tuy, Hai Bà Trưng, Hà Nội',20.99570000,105.86860000,'24/7','station-20260628211811-899faafb-5355-4a8f-9864-edabab3a88bf.jpg',4.0,2,1,'2026-06-28 19:49:25'),(135,'Hai Bà Trưng - Vinmec Times City','458 Minh Khai, Vĩnh Tuy, Hai Bà Trưng, Hà Nội',20.99650000,105.86900000,'24/7','station-20260628211828-aba03f36-73f7-4106-a235-0d87d8fa0f05.jpg',4.0,1,1,'2026-06-28 19:49:25'),(136,'Thanh Oai - KĐT Thanh Hà HH01','KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội',20.93200000,105.79500000,'24/7','station-20260628211856-b03204f5-db77-4066-b3ef-64ad48a812b2.png',4.7,3,1,'2026-06-28 19:49:25'),(137,'Thanh Oai - KĐT Thanh Hà HH03','KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội',20.93100000,105.79600000,'24/7','station-20260628211908-a7260792-084d-4a1a-8306-cbba0644b6d3.png',5.0,2,1,'2026-06-28 19:49:25'),(138,'Thanh Oai - Bãi đỗ xe Thanh Hà','KĐT Thanh Hà, Cự Khê, Thanh Oai, Hà Nội',20.92900000,105.79300000,'24/7','station-20260628211940-fb1631c0-13f6-415c-befd-c7272c1f821c.jpg',4.0,1,1,'2026-06-28 19:49:25'),(139,'Hoài Đức - Chung cư Splendora','Hầm B1, Splendora Bắc An Khánh, Hoài Đức, Hà Nội',21.00620000,105.72500000,'24/7','station-20260628212050-0ff4a1c9-d230-4492-b349-c9ba3970cea5.png',4.0,2,1,'2026-06-28 19:49:25'),(140,'Hoài Đức - Nam An Khánh','KĐT Nam An Khánh, An Khánh, Hoài Đức, Hà Nội',20.99900000,105.71800000,'24/7','station-20260628212102-9868ca6c-b617-46bd-8527-7963ef53fe03.jpg',4.0,1,1,'2026-06-28 19:49:25'),(141,'Ba Vì - Trạm dừng nghỉ Sữa Ất Thảo 2','Láng Hòa Lạc, Yên Bài, Ba Vì, Hà Nội',21.01250000,105.47950000,'24/7','station-20260628212225-e70232f2-2989-4433-8305-aae746f6bf6e.jpg',4.3,3,1,'2026-06-28 19:49:25'),(142,'Ba Vì - Sữa Chị Vàng','QL32, Tản Lĩnh, Ba Vì, Hà Nội',21.09650000,105.38500000,'24/7','station-20260628212256-a068649d-74c6-4f84-9a51-27589e96e2c2.jpg',5.0,1,1,'2026-06-28 19:49:25'),(143,'Ba Vì - Cây xăng Phú Sơn','QL32, Phú Sơn, Ba Vì, Hà Nội',21.14400000,105.37800000,'24/7','station-20260628212122-e7d58431-af7c-4572-a772-a3bfa77b3cc2.jpg',4.5,2,1,'2026-06-28 19:49:25'),(144,'Bắc Từ Liêm - Chung cư C2 Xuân Đỉnh','Đường Đỗ Nhuận, Xuân Đỉnh, Bắc Từ Liêm, Hà Nội',21.07400000,105.79800000,'24/7','station-20260628212136-ee2895c8-18db-430b-a766-5630ea8cc6d4.jpg',4.0,1,1,'2026-06-28 19:49:25'),(145,'Bắc Từ Liêm - Vincom Bắc Từ Liêm','234 Phạm Văn Đồng, Cổ Nhuế, Bắc Từ Liêm, Hà Nội',21.05050000,105.78200000,'24/7','station-20260628212311-65d2ba8f-7153-4559-8fac-fae6e1905f71.jpg',4.0,1,1,'2026-06-28 19:49:25'),(146,'Bắc Từ Liêm - Showroom VinFast Phạm Văn Đồng','166 Phạm Văn Đồng, Xuân Đỉnh, Bắc Từ Liêm, Hà Nội',21.04500000,105.78350000,'24/7','station-20260628212322-d81842f0-f8b9-462c-a68d-312cbff90209.jpg',5.0,1,1,'2026-06-28 19:49:25'),(147,'Sóc Sơn - Bãi đỗ xe Gia Linh','TT Sóc Sơn, Sóc Sơn, Hà Nội',21.25800000,105.85000000,'24/7','station-20260628212332-aed6f5ad-314b-4263-8c28-9fa40760db81.jpg',4.0,1,1,'2026-06-28 19:49:25'),(148,'Sóc Sơn - Cây xăng Total Phú Minh','Sân bay Nội Bài, Phú Minh, Sóc Sơn, Hà Nội',21.21850000,105.81100000,'24/7','station-20260628212343-f7103fcb-0c6e-4cd3-94cc-91dd6a533c3e.jpg',4.7,3,1,'2026-06-28 19:49:25'),(149,'a','a',1.00000000,1.00000000,'24/7','station-20260628194946-686c09de-2799-465a-81e8-12e911d12525.jpg',4.7,3,1,'2026-06-28 19:49:49'),(150,'a','a',1.00000000,1.00000000,'24/7','station-20260628195358-bb93b2e3-05ad-4c47-887e-5fcb0a4ca02b.jpg',4.7,3,1,'2026-06-28 19:54:00'),(151,'j','j',8.00000000,8.00000000,'24/7','',4.5,2,0,'2026-06-28 20:11:41');
/*!40000 ALTER TABLE `charging_stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `connector_types`
--

DROP TABLE IF EXISTS `connector_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `connector_types` (
  `connector_id` bigint NOT NULL AUTO_INCREMENT,
  `station_id` bigint NOT NULL,
  `type` varchar(20) NOT NULL,
  `power_kw` int NOT NULL,
  `total_ports` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`connector_id`),
  KEY `station_id` (`station_id`),
  CONSTRAINT `connector_types_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `charging_stations` (`station_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=223 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `connector_types`
--

LOCK TABLES `connector_types` WRITE;
/*!40000 ALTER TABLE `connector_types` DISABLE KEYS */;
INSERT INTO `connector_types` VALUES (154,133,'CCS2',60,4),(172,149,'CCS2',60,4),(173,150,'CCS2',60,4),(174,151,'CCS2',60,4),(175,104,'CCS2',60,4),(177,107,'CCS2',60,4),(178,105,'CCS2',60,4),(179,108,'CCS2',60,4),(180,109,'CCS2',60,4),(182,110,'CCS2',60,4),(183,111,'CCS2',60,4),(184,112,'CCS2',60,4),(185,113,'CCS2',60,4),(186,114,'CCS2',60,4),(187,115,'CCS2',60,4),(188,116,'CCS2',60,4),(189,117,'CCS2',60,4),(190,119,'CCS2',60,4),(191,118,'CCS2',60,4),(192,120,'CCS2',60,4),(193,121,'CCS2',60,4),(194,122,'CCS2',60,4),(195,123,'CCS2',250,2),(196,124,'CCS2',60,4),(197,125,'CCS2',60,4),(198,126,'CCS2',60,4),(199,127,'CCS2',60,4),(201,128,'AC',11,4),(202,129,'AC',11,4),(203,130,'AC',11,4),(204,131,'CCS2',60,4),(205,132,'CCS2',60,4),(206,134,'CCS2',60,4),(207,135,'CCS2',60,4),(208,136,'CCS2',60,2),(209,136,'AC',22,2),(210,137,'CCS2',60,2),(211,137,'AC',22,2),(212,138,'CCS2',60,4),(213,139,'AC',11,4),(214,140,'CCS2',60,4),(215,143,'CCS2',60,4),(216,144,'CCS2',60,4),(217,141,'CCS2',60,4),(218,142,'CCS2',60,4),(219,145,'CCS2',60,4),(220,146,'CCS2',60,4),(221,147,'CCS2',60,4),(222,148,'CCS2',60,4);
/*!40000 ALTER TABLE `connector_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `station_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `comment` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`review_id`),
  KEY `user_id` (`user_id`),
  KEY `station_id` (`station_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `charging_stations` (`station_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (5,10,133,5,'Trạm sạc nằm ở vị trí hầm gửi xe Vincom Phạm Ngọc Thạch rất tiện lợi và dễ tìm, sạc nhanh và ổn định.','2026-06-28 19:52:55'),(6,10,104,4,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:51'),(7,14,104,5,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:51'),(8,13,104,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:51'),(9,12,105,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:52'),(10,14,105,5,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:52'),(11,10,105,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:52'),(12,13,107,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:52'),(13,10,107,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:52'),(14,12,107,4,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:52'),(15,13,108,4,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:52'),(16,12,108,4,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:52'),(17,15,108,4,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:52'),(18,15,109,5,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:52'),(19,12,109,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:52'),(20,15,110,4,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:52'),(21,15,111,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:52'),(22,14,111,4,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:52'),(23,10,112,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:52'),(24,12,112,5,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:52'),(25,14,112,5,'Rất hài lòng, sạc đầy pin nhanh chóng.','2026-06-28 21:39:52'),(26,13,113,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:52'),(27,12,113,5,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:52'),(28,10,114,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:52'),(29,10,115,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:52'),(30,13,115,5,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:52'),(31,14,116,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:53'),(32,12,116,4,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:53'),(33,12,117,5,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:53'),(34,14,117,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:53'),(35,10,118,4,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:53'),(36,12,119,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:53'),(37,14,119,4,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:53'),(38,13,120,5,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:53'),(39,15,120,5,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:53'),(40,12,120,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:53'),(41,15,121,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:53'),(42,10,121,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:53'),(43,10,122,4,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:53'),(44,13,123,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:53'),(45,14,124,4,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:53'),(46,12,124,5,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:53'),(47,12,125,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:53'),(48,10,126,5,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:53'),(49,14,126,4,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:53'),(50,15,126,4,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:53'),(51,13,127,4,'Rất hài lòng, sạc đầy pin nhanh chóng.','2026-06-28 21:39:53'),(52,12,127,4,'Rất hài lòng, sạc đầy pin nhanh chóng.','2026-06-28 21:39:53'),(53,14,127,4,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:53'),(54,12,128,5,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:53'),(55,15,129,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(56,10,129,5,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:54'),(57,14,129,5,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(58,15,130,4,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:54'),(59,15,131,5,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:54'),(60,14,131,5,'Trạm sạc rất nhanh, vị trí dễ tìm.','2026-06-28 21:39:54'),(61,12,132,4,'Rất hài lòng, sạc đầy pin nhanh chóng.','2026-06-28 21:39:54'),(62,13,132,4,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:54'),(63,14,134,4,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:54'),(64,10,134,4,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:54'),(65,14,135,4,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:54'),(66,15,136,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:54'),(67,14,136,5,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:54'),(68,13,136,4,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:54'),(69,15,137,5,'Sạc ổn định, không bị ngắt quãng giữa chừng.','2026-06-28 21:39:54'),(70,14,137,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:54'),(71,12,138,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(72,15,139,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(73,12,139,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(74,13,140,4,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:54'),(75,10,141,5,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:54'),(76,14,141,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:54'),(77,15,141,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:54'),(78,13,142,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:55'),(79,15,143,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:55'),(80,13,143,5,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:55'),(81,10,144,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:55'),(82,14,145,4,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:55'),(83,12,146,5,'Vị trí đỗ xe rộng rãi, sạc nhanh và ổn định.','2026-06-28 21:39:55'),(84,14,147,4,'Vị trí thuận tiện, có nhân viên hướng dẫn nhiệt tình.','2026-06-28 21:39:55'),(85,15,148,5,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:55'),(86,14,148,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:55'),(87,12,148,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:55'),(88,14,149,5,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:55'),(89,13,149,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:55'),(90,10,149,4,'Vị trí hầm hơi nóng một chút nhưng công suất sạc rất tốt.','2026-06-28 21:39:55'),(91,10,150,4,'Trạm sạch sẽ, an toàn, chất lượng súng sạc tốt.','2026-06-28 21:39:55'),(92,15,150,5,'Rất hài lòng, sạc đầy pin nhanh chóng.','2026-06-28 21:39:55'),(93,12,150,5,'Súng sạc dễ cắm rút, màn hình hiển thị thông tin rõ ràng.','2026-06-28 21:39:55'),(94,10,151,5,'Trụ sạc hoạt động tốt, dịch vụ quanh đây rất tiện lợi.','2026-06-28 21:39:55'),(95,14,151,4,'Tiện đường đi làm, giá cả dịch vụ hợp lý.','2026-06-28 21:39:55'),(96,1,117,5,'hơi rởm','2026-06-28 22:47:06');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_station_history`
--

DROP TABLE IF EXISTS `user_station_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_station_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `station_id` bigint NOT NULL,
  `visit_count` int NOT NULL DEFAULT '1',
  `last_visited` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`history_id`),
  UNIQUE KEY `uq_user_station_hist` (`user_id`,`station_id`),
  KEY `station_id` (`station_id`),
  CONSTRAINT `user_station_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_station_history_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `charging_stations` (`station_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_station_history`
--

LOCK TABLES `user_station_history` WRITE;
/*!40000 ALTER TABLE `user_station_history` DISABLE KEYS */;
INSERT INTO `user_station_history` VALUES (16,10,133,1,'2026-06-28 19:52:55'),(17,10,104,1,'2026-06-28 21:39:51'),(18,14,104,1,'2026-06-28 21:39:51'),(19,13,104,1,'2026-06-28 21:39:51'),(20,12,105,1,'2026-06-28 21:39:52'),(21,14,105,1,'2026-06-28 21:39:52'),(22,10,105,1,'2026-06-28 21:39:52'),(23,13,107,1,'2026-06-28 21:39:52'),(24,10,107,1,'2026-06-28 21:39:52'),(25,12,107,1,'2026-06-28 21:39:52'),(26,13,108,1,'2026-06-28 21:39:52'),(27,12,108,1,'2026-06-28 21:39:52'),(28,15,108,1,'2026-06-28 21:39:52'),(29,15,109,1,'2026-06-28 21:39:52'),(30,12,109,1,'2026-06-28 21:39:52'),(31,15,110,1,'2026-06-28 21:39:52'),(32,15,111,1,'2026-06-28 21:39:52'),(33,14,111,1,'2026-06-28 21:39:52'),(34,10,112,1,'2026-06-28 21:39:52'),(35,12,112,1,'2026-06-28 21:39:52'),(36,14,112,1,'2026-06-28 21:39:52'),(37,13,113,1,'2026-06-28 21:39:52'),(38,12,113,1,'2026-06-28 21:39:52'),(39,10,114,1,'2026-06-28 21:39:52'),(40,10,115,1,'2026-06-28 21:39:52'),(41,13,115,1,'2026-06-28 21:39:52'),(42,14,116,1,'2026-06-28 21:39:52'),(43,12,116,1,'2026-06-28 21:39:53'),(44,12,117,1,'2026-06-28 21:39:53'),(45,14,117,1,'2026-06-28 21:39:53'),(46,10,118,1,'2026-06-28 21:39:53'),(47,12,119,1,'2026-06-28 21:39:53'),(48,14,119,1,'2026-06-28 21:39:53'),(49,13,120,1,'2026-06-28 21:39:53'),(50,15,120,1,'2026-06-28 21:39:53'),(51,12,120,1,'2026-06-28 21:39:53'),(52,15,121,1,'2026-06-28 21:39:53'),(53,10,121,1,'2026-06-28 21:39:53'),(54,10,122,1,'2026-06-28 21:39:53'),(55,13,123,1,'2026-06-28 21:39:53'),(56,14,124,1,'2026-06-28 21:39:53'),(57,12,124,1,'2026-06-28 21:39:53'),(58,12,125,1,'2026-06-28 21:39:53'),(59,10,126,1,'2026-06-28 21:39:53'),(60,14,126,1,'2026-06-28 21:39:53'),(61,15,126,1,'2026-06-28 21:39:53'),(62,13,127,1,'2026-06-28 21:39:53'),(63,12,127,1,'2026-06-28 21:39:53'),(64,14,127,1,'2026-06-28 21:39:53'),(65,12,128,1,'2026-06-28 21:39:53'),(66,15,129,1,'2026-06-28 21:39:54'),(67,10,129,1,'2026-06-28 21:39:54'),(68,14,129,1,'2026-06-28 21:39:54'),(69,15,130,1,'2026-06-28 21:39:54'),(70,15,131,1,'2026-06-28 21:39:54'),(71,14,131,1,'2026-06-28 21:39:54'),(72,12,132,1,'2026-06-28 21:39:54'),(73,13,132,1,'2026-06-28 21:39:54'),(74,14,134,1,'2026-06-28 21:39:54'),(75,10,134,1,'2026-06-28 21:39:54'),(76,14,135,1,'2026-06-28 21:39:54'),(77,15,136,1,'2026-06-28 21:39:54'),(78,14,136,1,'2026-06-28 21:39:54'),(79,13,136,1,'2026-06-28 21:39:54'),(80,15,137,1,'2026-06-28 21:39:54'),(81,14,137,1,'2026-06-28 21:39:54'),(82,12,138,1,'2026-06-28 21:39:54'),(83,15,139,1,'2026-06-28 21:39:54'),(84,12,139,1,'2026-06-28 21:39:54'),(85,13,140,1,'2026-06-28 21:39:54'),(86,10,141,1,'2026-06-28 21:39:54'),(87,14,141,1,'2026-06-28 21:39:54'),(88,15,141,1,'2026-06-28 21:39:54'),(89,13,142,1,'2026-06-28 21:39:55'),(90,15,143,1,'2026-06-28 21:39:55'),(91,13,143,1,'2026-06-28 21:39:55'),(92,10,144,1,'2026-06-28 21:39:55'),(93,14,145,1,'2026-06-28 21:39:55'),(94,12,146,1,'2026-06-28 21:39:55'),(95,14,147,1,'2026-06-28 21:39:55'),(96,15,148,1,'2026-06-28 21:39:55'),(97,14,148,1,'2026-06-28 21:39:55'),(98,12,148,1,'2026-06-28 21:39:55'),(99,14,149,1,'2026-06-28 21:39:55'),(100,13,149,1,'2026-06-28 21:39:55'),(101,10,149,1,'2026-06-28 21:39:55'),(102,10,150,1,'2026-06-28 21:39:55'),(103,15,150,1,'2026-06-28 21:39:55'),(104,12,150,1,'2026-06-28 21:39:55'),(105,10,151,1,'2026-06-28 21:39:55'),(106,14,151,1,'2026-06-28 21:39:55'),(107,1,109,1,'2026-06-28 22:28:49'),(108,1,132,1,'2026-06-28 22:34:39'),(109,1,117,2,'2026-06-28 22:49:58'),(110,1,133,2,'2026-06-28 23:02:22');
/*!40000 ALTER TABLE `user_station_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `vehicle_model` varchar(50) DEFAULT NULL,
  `connector_type` varchar(20) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `gender` varchar(10) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `avatar_url` text,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Nguyễn Thị Đạt','datnt@gmail.com','$2a$10$ZHIeoSzbbG2QadDwH8fRQ.n2.Uom0UFL72nsx0u.OKly/3O0sVOwq','0988111222','VF 8',NULL,'2026-04-09 05:32:29',1,'Nữ','1999-05-13','avatar-20260628192946-2a55b3f1-e138-439b-8351-de259ae97a83.webp',''),(2,'Vi Van Quy','vivanquy20804@gmail.com','$2a$10$ONwvQOLw5D7i9onbozo7bOgxfz4tpv7i1I96Vo9zXjPsRTH8R70ky','0901230241','VF 3','CCS2','2026-04-24 09:13:45',1,'MALE','2000-01-01',NULL,''),(3,'Nguyễn Văn Đạt','datnv1@gmail.com','$2a$10$ZS8u3wP9oxOMWPMJ.xzLUOrkv9hEaDM1ulpluT9gvvt776Dvg17EK','0988111221','VF 8','CCS2','2026-04-24 09:26:18',1,'male','1999-05-12','',''),(4,'Quy','quy@gmail.com','$2a$10$nKDE4BKn1OAQNSM4KApbb.8E36XXu/4s4QshrZ5a7cx20E4O75dua','0987218428','VF 3','CCS2','2026-04-24 11:12:08',1,'MALE','2000-01-01',NULL,''),(7,'Smoke User','smoke45762@example.com','$2a$10$fLdQMIJF/zM4hRzpVV7yK.xIjatfoQ8RJJecYt2XdC3Qhbltx13..','0942646364','VF 8','CCS2','2026-05-29 04:11:01',1,'Male',NULL,NULL,''),(8,'Quản trị viên','admin@evcpoint.vn','$2a$10$NOhhy4XuE3tEe9Aw8csGouA434hm93ZrgRUIcWV3fcRQd8.bJEsjy','0999999999',NULL,NULL,'2026-06-07 00:35:50',1,NULL,NULL,NULL,'ADMIN'),(9,'Vi Văn Quý','vanq@gmail.com','$2a$10$wXlrsn/lv9Ds5zgIUBs0Ieyvc/vzOJWjRYettj4MRCxfNIYW/yP7u','0123456789','VF 3','AC (Type 2)','2026-06-08 08:36:45',1,'MALE','2000-01-01',NULL,'USER'),(10,'Nguyễn Văn A','nguyenvana@gmail.com','$2a$10$8NEqosFEjOfJ13u.W5fW3.LPInXM4UwnMJlvMekaboHaLEnlCInee','0912345678',NULL,NULL,'2026-06-08 08:46:58',1,NULL,NULL,NULL,'USER'),(11,'Hoà','hoa1@gmail.com','$2a$10$vfXffBHNmoDhpQX5l2yXSeZpreCr.Y19fuMA2QuHgcFg96qvS6T3q','0986456461','VF 8',NULL,'2026-06-28 13:19:40',1,'MALE','2000-01-01','http://shimmer-itinerary-spilt.ngrok-free.dev/uploads/avatars/avatar-20260628131753-f2aa3c7f-6182-423f-85f2-c9de1429b4f2.jpg','USER'),(12,'Trần Thị B','tranthib@gmail.com','$2a$10$YqetjtjoKwNWMYjAmIxdoOUD7wdjLaBhcDjeIPy/k9kuVOPn3vJmW','0987654321',NULL,NULL,'2026-06-28 21:39:51',1,NULL,NULL,NULL,'USER'),(13,'Lê Văn C','levanc@gmail.com','$2a$10$Hklv96i9JlO6Eie0XPSuHe9xDX.44oSZkyh9TC77InDUzTD2.sPcC','0901122334',NULL,NULL,'2026-06-28 21:39:51',1,NULL,NULL,NULL,'USER'),(14,'Phạm Văn D','phamvand@gmail.com','$2a$10$HOlVqBZNHdUN4zz5sT9SgOGW5j.fVOPu9B6u.9aFmE2PVTaAB89ce','0934455667',NULL,NULL,'2026-06-28 21:39:51',1,NULL,NULL,NULL,'USER'),(15,'Hoàng Thị E','hoangthie@gmail.com','$2a$10$o5SqrPrZAmIvTjvUf1DuCuRnIwcpTVkXqMRRau7OfI92TIRrJ2/7G','0978899001',NULL,NULL,'2026-06-28 21:39:51',1,NULL,NULL,NULL,'USER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'vinfast_charging'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-28 23:41:54
