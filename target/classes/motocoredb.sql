CREATE DATABASE  IF NOT EXISTS `motocoredb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `motocoredb`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: motocoredb
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alerts`
--

DROP TABLE IF EXISTS `alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alerts` (
  `alertId` int NOT NULL AUTO_INCREMENT,
  `alertType` enum('Low stock','Upcoming appointment') NOT NULL,
  `message` text NOT NULL,
  `generatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `readAt` timestamp NULL DEFAULT NULL,
  `status` enum('Pending','Read','Resolved') DEFAULT 'Pending',
  `referenceId` int NOT NULL,
  `referenceType` enum('Product','Appointment') NOT NULL,
  PRIMARY KEY (`alertId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerts`
--

LOCK TABLES `alerts` WRITE;
/*!40000 ALTER TABLE `alerts` DISABLE KEYS */;
INSERT INTO `alerts` VALUES (1,'Low stock','El producto \'Llanta\' ha alcanzado o bajado del nivel mínimo de stock.','2025-04-28 20:22:36',NULL,'Pending',1,'Product'),(2,'Low stock','El producto \'Llanta\' ha alcanzado o bajado del nivel mínimo de stock.','2025-05-03 07:11:35',NULL,'Pending',1,'Product'),(3,'Upcoming appointment','Nueva cita agendada para el cliente \'Carlos SA\' con la motocicleta placa \'das214\' el día 2025-05-06','2025-05-03 07:12:21',NULL,'Pending',0,'Appointment');
/*!40000 ALTER TABLE `alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointmentservices`
--

DROP TABLE IF EXISTS `appointmentservices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointmentservices` (
  `appointmentServiceId` int NOT NULL AUTO_INCREMENT,
  `appointmentId` int NOT NULL,
  `serviceId` int NOT NULL,
  `chargedPrice` decimal(10,2) NOT NULL,
  `notes` text,
  PRIMARY KEY (`appointmentServiceId`),
  KEY `appointmentId` (`appointmentId`),
  KEY `serviceId` (`serviceId`),
  CONSTRAINT `appointmentservices_ibfk_1` FOREIGN KEY (`appointmentId`) REFERENCES `workshopappointments` (`appointmentId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointmentservices`
--

LOCK TABLES `appointmentservices` WRITE;
/*!40000 ALTER TABLE `appointmentservices` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointmentservices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customerId` int NOT NULL AUTO_INCREMENT,
  `customerType` enum('Individual','Company') NOT NULL,
  `nameOrCompany` varchar(100) NOT NULL,
  `identityDocument` varchar(20) DEFAULT NULL,
  `address` varchar(150) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `purchaseCount` int DEFAULT '0',
  `status` enum('Active','Inactive') DEFAULT 'Active',
  PRIMARY KEY (`customerId`),
  UNIQUE KEY `identityDocument` (`identityDocument`),
  KEY `idxCustomersDocument` (`identityDocument`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Individual','Carlos SA','16219004','','31773312770','caevam@gmail.com','2025-04-28 10:55:18',0,'Active'),(2,'Individual','Esteban','1113859531','fdds455','3150566593','esvca','2025-05-03 02:14:01',0,'Active');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventorymovements`
--

DROP TABLE IF EXISTS `inventorymovements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventorymovements` (
  `movementId` int NOT NULL AUTO_INCREMENT,
  `productId` int NOT NULL,
  `movementType` enum('In','Out') NOT NULL,
  `quantity` int NOT NULL,
  `movementDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `userId` int NOT NULL,
  `referenceId` int NOT NULL,
  `referenceType` enum('Purchase','Sale','Adjustment') NOT NULL,
  `notes` text,
  PRIMARY KEY (`movementId`),
  KEY `productId` (`productId`),
  KEY `userId` (`userId`),
  KEY `idxMovementsDate` (`movementDate`),
  CONSTRAINT `inventorymovements_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`),
  CONSTRAINT `inventorymovements_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventorymovements`
--

LOCK TABLES `inventorymovements` WRITE;
/*!40000 ALTER TABLE `inventorymovements` DISABLE KEYS */;
INSERT INTO `inventorymovements` VALUES (1,1,'In',90,'2025-04-28 10:39:36',1,1,'Adjustment','Movimiento inicial al agregar producto'),(2,2,'In',50,'2025-04-28 15:07:39',1,2,'Adjustment','Movimiento inicial al agregar producto'),(3,3,'In',80,'2025-05-03 02:13:25',1,3,'Adjustment','Movimiento inicial al agregar producto');
/*!40000 ALTER TABLE `inventorymovements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productcategories`
--

DROP TABLE IF EXISTS `productcategories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productcategories` (
  `categoryId` int NOT NULL AUTO_INCREMENT,
  `categoryName` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  PRIMARY KEY (`categoryId`),
  UNIQUE KEY `categoryName` (`categoryName`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productcategories`
--

LOCK TABLES `productcategories` WRITE;
/*!40000 ALTER TABLE `productcategories` DISABLE KEYS */;
INSERT INTO `productcategories` VALUES (1,'Aceites','Lubricantes para motor de motocicletas.','Active'),(2,'Baterías','Baterías para motos de diferentes cilindradas.','Active'),(3,'Frenos','Sistemas de freno: pastillas, discos, líquido de frenos.','Active'),(4,'Accesorios','Accesorios como cascos, guantes, chaquetas, etc.','Active'),(5,'Luces y Eléctricos','Farolas, bombillos LED, sistemas eléctricos.','Active'),(6,'Suspensión','Amortiguadores, resortes y componentes de suspensión.','Active'),(7,'Motor','Pistones, válvulas, bielas, kits de motor.','Active'),(8,'Transmisión','Cadenas, piñones, coronas, kits de transmisión.','Active'),(9,'Escape','Sistemas de escape, mofles deportivos.','Active'),(10,'Neumáticos','Llantas de diferentes medidas y marcas.','Active'),(11,'Filtros','Filtros de aceite, aire y gasolina.','Active'),(12,'Herramientas','Herramientas específicas para mantenimiento de motos.','Active'),(13,'Protecciones','Protecciones para manos, piernas, codos, rodillas.','Active'),(14,'Ropa Técnica','Chaquetas impermeables, pantalones de protección.','Active'),(15,'Electrónica','Alarmas, GPS, sistemas de rastreo.','Active'),(16,'Accesorios de Viaje','Maletas, parrillas, bolsos de tanque.','Active'),(17,'Cuidado y Limpieza','Productos de limpieza y cuidado de motos.','Active'),(18,'Bujías','Bujías estándar y de alto rendimiento.','Active'),(19,'Rodamientos','Rodamientos para ruedas, motor y transmisión.','Active'),(20,'Partes de Carenado','Plásticos y accesorios de carenado.','Active');
/*!40000 ALTER TABLE `productcategories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `productId` int NOT NULL AUTO_INCREMENT,
  `productCode` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text,
  `categoryId` int DEFAULT NULL,
  `purchasePrice` decimal(10,2) NOT NULL,
  `salePrice` decimal(10,2) NOT NULL,
  `currentStock` int NOT NULL DEFAULT '0',
  `minStock` int NOT NULL DEFAULT '5',
  `supplierId` int DEFAULT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`productId`),
  UNIQUE KEY `productCode` (`productCode`),
  KEY `categoryId` (`categoryId`),
  KEY `supplierId` (`supplierId`),
  KEY `idxProductsName` (`name`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`categoryId`) REFERENCES `productcategories` (`categoryId`),
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'6581655','Llanta','',1,80000.00,90000.00,1,5,1,'Active','2025-04-28 10:39:36','2025-05-03 02:11:35'),(2,'65465165','Motor','',1,1500000.00,2000000.00,35,5,1,'Active','2025-04-28 15:07:39','2025-04-29 22:03:15'),(3,'684862481','Espejo','par de espejos',16,12000.00,15000.00,80,5,5,'Active','2025-05-03 02:13:25',NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchasedetails`
--

DROP TABLE IF EXISTS `purchasedetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchasedetails` (
  `purchaseDetailId` int NOT NULL AUTO_INCREMENT,
  `purchaseId` int NOT NULL,
  `productId` int NOT NULL,
  `quantity` int NOT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`purchaseDetailId`),
  KEY `purchaseId` (`purchaseId`),
  KEY `productId` (`productId`),
  CONSTRAINT `purchasedetails_ibfk_1` FOREIGN KEY (`purchaseId`) REFERENCES `purchases` (`purchaseId`) ON DELETE CASCADE,
  CONSTRAINT `purchasedetails_ibfk_2` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchasedetails`
--

LOCK TABLES `purchasedetails` WRITE;
/*!40000 ALTER TABLE `purchasedetails` DISABLE KEYS */;
INSERT INTO `purchasedetails` VALUES (1,1,1,90,80000.00,7200000.00),(2,2,1,90,80000.00,7200000.00),(3,3,1,78,80000.00,6240000.00),(4,4,1,78,80000.00,6240000.00),(5,5,2,50,1500000.00,75000000.00),(6,6,1,78,80000.00,6240000.00),(7,7,2,35,1500000.00,52500000.00),(8,8,3,80,12000.00,960000.00);
/*!40000 ALTER TABLE `purchasedetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchases` (
  `purchaseId` int NOT NULL AUTO_INCREMENT,
  `invoiceNumber` varchar(20) DEFAULT NULL,
  `purchaseDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `supplierId` int NOT NULL,
  `userId` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `tax` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `status` enum('Pending','Received','Cancelled') DEFAULT 'Pending',
  `notes` text,
  PRIMARY KEY (`purchaseId`),
  KEY `supplierId` (`supplierId`),
  KEY `userId` (`userId`),
  KEY `idxPurchasesDate` (`purchaseDate`),
  CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`supplierId`) REFERENCES `suppliers` (`supplierId`),
  CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchases`
--

LOCK TABLES `purchases` WRITE;
/*!40000 ALTER TABLE `purchases` DISABLE KEYS */;
INSERT INTO `purchases` VALUES (1,'AUTO-1745836776433','2025-04-28 10:39:36',2,1,7200000.00,1368000.00,8568000.00,'Received','Compra generada automáticamente tras agregar el producto.'),(2,'AUTO-1745836787039','2025-04-28 10:39:47',2,1,7200000.00,1368000.00,8568000.00,'Received','Compra generada automáticamente tras editar el producto.'),(3,'AUTO-1745848042199','2025-04-28 13:47:22',2,1,6240000.00,1185600.00,7425600.00,'Received','Compra generada automáticamente tras editar el producto.'),(4,'AUTO-1745851302780','2025-04-28 14:41:42',1,1,6240000.00,1185600.00,7425600.00,'Received','Compra generada automáticamente tras editar el producto.'),(5,'AUTO-1745852859154','2025-04-28 15:07:39',1,1,75000000.00,14250000.00,89250000.00,'Received','Compra generada automáticamente tras agregar el producto.'),(6,'AUTO-1745853413686','2025-04-28 15:16:53',1,1,6240000.00,1185600.00,7425600.00,'Received','Compra generada automáticamente tras editar el producto.'),(7,'AUTO-1745964197421','2025-04-29 22:03:17',1,1,52500000.00,9975000.00,62475000.00,'Received','Compra generada automáticamente tras editar el producto.'),(8,'AUTO-1746238405796','2025-05-03 02:13:25',5,1,960000.00,182400.00,1142400.00,'Received','Compra generada automáticamente tras agregar el producto.');
/*!40000 ALTER TABLE `purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saledetails`
--

DROP TABLE IF EXISTS `saledetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `saledetails` (
  `saleDetailId` int NOT NULL AUTO_INCREMENT,
  `saleId` int NOT NULL,
  `productId` int NOT NULL,
  `quantity` int NOT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`saleDetailId`),
  KEY `saleId` (`saleId`),
  KEY `productId` (`productId`),
  CONSTRAINT `saledetails_ibfk_1` FOREIGN KEY (`saleId`) REFERENCES `sales` (`saleId`) ON DELETE CASCADE,
  CONSTRAINT `saledetails_ibfk_2` FOREIGN KEY (`productId`) REFERENCES `products` (`productId`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saledetails`
--

LOCK TABLES `saledetails` WRITE;
/*!40000 ALTER TABLE `saledetails` DISABLE KEYS */;
INSERT INTO `saledetails` VALUES (1,1,1,12,90000.00,1080000.00),(2,2,2,15,2000000.00,30000000.00),(3,3,2,40,2000000.00,80000000.00),(4,4,1,46,90000.00,4140000.00),(5,5,1,30,90000.00,2700000.00),(6,6,1,12,90000.00,1080000.00),(7,7,1,1,90000.00,90000.00);
/*!40000 ALTER TABLE `saledetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales` (
  `saleId` int NOT NULL AUTO_INCREMENT,
  `invoiceNumber` varchar(20) NOT NULL,
  `saleDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `customerId` int NOT NULL,
  `userId` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `tax` decimal(10,2) NOT NULL,
  `discount` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL,
  `paymentMethod` enum('Cash','Card','Transfer','Other') NOT NULL,
  `status` enum('Completed','Cancelled') DEFAULT 'Completed',
  `notes` text,
  PRIMARY KEY (`saleId`),
  UNIQUE KEY `invoiceNumber` (`invoiceNumber`),
  KEY `customerId` (`customerId`),
  KEY `userId` (`userId`),
  KEY `idxSalesDate` (`saleDate`),
  CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customers` (`customerId`),
  CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales`
--

LOCK TABLES `sales` WRITE;
/*!40000 ALTER TABLE `sales` DISABLE KEYS */;
INSERT INTO `sales` VALUES (1,'INV-1745838600192','2025-04-28 16:10:00',1,1,1080000.00,205200.00,0.00,1285200.00,'Cash','Completed',NULL),(2,'INV-1745853566493','2025-04-28 20:19:26',1,2,30000000.00,5700000.00,0.00,35700000.00,'Cash','Completed',NULL),(3,'INV-1745853662110','2025-04-28 20:21:02',1,1,80000000.00,15200000.00,0.00,95200000.00,'Cash','Completed',NULL),(4,'INV-1745853684520','2025-04-28 20:21:25',1,1,4140000.00,786600.00,0.00,4926600.00,'Cash','Completed',NULL),(5,'INV-1745853756029','2025-04-28 20:22:36',1,1,2700000.00,513000.00,0.00,3213000.00,'Cash','Completed',NULL),(6,'INV-1746238288181','2025-05-03 07:11:28',1,2,1080000.00,205200.00,0.00,1285200.00,'Cash','Completed',NULL),(7,'INV-1746238295245','2025-05-03 07:11:35',1,2,90000.00,17100.00,0.00,107100.00,'Cash','Completed',NULL);
/*!40000 ALTER TABLE `sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `staffId` int NOT NULL AUTO_INCREMENT,
  `fullName` varchar(100) NOT NULL,
  `identityDocument` varchar(20) NOT NULL,
  `position` varchar(50) NOT NULL,
  `specialty` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(150) DEFAULT NULL,
  `hireDate` date NOT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `userId` int DEFAULT NULL,
  PRIMARY KEY (`staffId`),
  UNIQUE KEY `identityDocument` (`identityDocument`),
  KEY `staff_ibfk_1` (`userId`),
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (1,'Esteban','1113859631','Mecánico','','3185740578','esvca23@gmail.com','','2025-04-28','Active',2),(2,'Andrea','153218652','Vendedor','Administracion','315954865','anvapa','','2025-05-02','Active',NULL);
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistics`
--

DROP TABLE IF EXISTS `statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistics` (
  `statisticId` int NOT NULL AUTO_INCREMENT,
  `statisticType` enum('Sales','Inventory','Appointments') NOT NULL,
  `period` enum('Daily','Weekly','Monthly') NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `numericValue` decimal(15,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `generatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`statisticId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistics`
--

LOCK TABLES `statistics` WRITE;
/*!40000 ALTER TABLE `statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `supplierId` int NOT NULL AUTO_INCREMENT,
  `companyName` varchar(100) NOT NULL,
  `taxId` varchar(20) NOT NULL,
  `contactPerson` varchar(100) DEFAULT NULL,
  `contactPhone` varchar(20) DEFAULT NULL,
  `contactEmail` varchar(100) DEFAULT NULL,
  `address` varchar(150) DEFAULT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplierId`),
  UNIQUE KEY `taxId` (`taxId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'Proveedor Repuestos MotoFast','123456789','Juan Pérez','3001234567','juanperez@motofast.com','Calle 123 #45-67','Active','2025-04-28 10:36:35'),(2,'Distribuciones MotoParts','987654321','Ana García','3019876543','anagarcia@motoparts.com','Carrera 89 #12-34','Inactive','2025-04-28 10:36:35'),(3,'MotoMundo Importaciones','1122334455','Carlos Ruiz','3023456789','carlosruiz@motomundo.com','Avenida Principal 101','Active','2025-04-28 10:36:35'),(4,'SpeedMoto Supplies','5566778899','Laura Martínez','3101239876','lauramartinez@speedmoto.com','Diagonal 77 #66-55','Active','2025-04-28 10:36:35'),(5,'MundoMotorcycles S.A.','6677889900','Pedro Alvarez','3156549870','pedroalvarez@mundomotor.com','Transversal 45 #78-12','Active','2025-04-28 10:36:35'),(6,'ProMoto Equipos','7788990011','Mariana Castro','3162349870','marianacastro@promotoequipos.com','Calle 50 #23-89','Active','2025-04-28 10:36:35'),(7,'Importadora MotoKing','8899001122','David Suárez','3173456780','davidsuarez@motoking.com','Carrera 10 #11-22','Active','2025-04-28 10:36:35'),(8,'TopMoto Suministros','9900112233','Paula Torres','3184567890','paulatorres@topmoto.com','Avenida de las Américas #98-76','Active','2025-04-28 10:36:35'),(9,'AutoBike Colombia','1011121314','Andrés Herrera','3195678901','andresherrera@autobike.com','Calle 8 #80-60','Active','2025-04-28 10:36:35'),(10,'FullMoto Importaciones','1213141516','Diana López','3206789012','dianalopez@fullmoto.com','Carrera 25 #15-70','Active','2025-04-28 10:36:35');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `fullName` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Administrador','Vendedor','Mecánico') NOT NULL,
  `status` enum('Active','Inactive') DEFAULT 'Active',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastLogin` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Super Admin','root','AgVVSxy9e9qYlp6F2JGePNir7/RkRjwHqnzDqKguOS/0Qv/6zFNgQnthATOrfzPj','Administrador','Active','2025-04-28 10:37:13','2025-05-03 07:15:25'),(2,'Esteban','esvca','93rTn+SQveuWJ9fzjPDMB+r7GmFiUzw/Ji/DVRVb2O5Qprp/oNCNterDszDLCNLs','Mecánico','Active','2025-04-28 10:42:29','2025-05-03 07:14:13');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workshopappointments`
--

DROP TABLE IF EXISTS `workshopappointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workshopappointments` (
  `appointmentId` int NOT NULL AUTO_INCREMENT,
  `customerId` int NOT NULL,
  `scheduledDate` date NOT NULL,
  `scheduledTime` time NOT NULL,
  `visitReason` varchar(255) NOT NULL,
  `motorcycleDescription` varchar(100) DEFAULT NULL,
  `motorcyclePlate` varchar(20) DEFAULT NULL,
  `status` enum('Scheduled','In progress','Completed','Cancelled') DEFAULT 'Scheduled',
  `userId` int DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`appointmentId`),
  KEY `customerId` (`customerId`),
  KEY `userId` (`userId`),
  KEY `idxAppointmentsDate` (`scheduledDate`),
  CONSTRAINT `workshopappointments_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customers` (`customerId`),
  CONSTRAINT `workshopappointments_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workshopappointments`
--

LOCK TABLES `workshopappointments` WRITE;
/*!40000 ALTER TABLE `workshopappointments` DISABLE KEYS */;
INSERT INTO `workshopappointments` VALUES (1,1,'2025-05-06','21:00:00','Mantenimiento','tecno','das214','Scheduled',1,'');
/*!40000 ALTER TABLE `workshopappointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'motocoredb'
--

--
-- Dumping routines for database 'motocoredb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-02 21:28:09
