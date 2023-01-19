DROP TABLE IF EXISTS `tb_service`;
CREATE TABLE `tb_service` (
  `id` bigint NOT NULL,
  `city` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) NOT NULL,
  `price` double DEFAULT NULL,
  `status` int NOT NULL,
  `type` int NOT NULL,
  `patient_id` bigint DEFAULT NULL,
  `staff_id` bigint DEFAULT NULL
)