CREATE TABLE `raw_data` (
  `id` bigint COMMENT '主键ID',
  `driver_id` bigint  COMMENT '驱动ID',
  `sys_id` bigint COMMENT '系统ID',
  `device_id` bigint COMMENT '设备ID',
  `colp_id` bigint COMMENT '项目ID',
  `point_id` bigint COMMENT '采集点ID',
  `data_time` datetime COMMENT '数据时间',
  `data_type` varchar(32) COMMENT '数据类型 text/blob',
  `raw_data` varchar(200) COMMENT '原始数据',
  `numeric_data` double DEFAULT NULL COMMENT '数值型原始数据',
  `processed_data` varchar(200) COMMENT '经过处理的数据',
  `status` smallint COMMENT '状态'
) ENGINE=olap
PARTITION BY RANGE(data_time) ()
DISTRIBUTED BY HASH(data_time)
PROPERTIES
(
 "dynamic_partition.enable" = "true",
 "dynamic_partition.time_unit" = "MONTH",
 "dynamic_partition.end" = "2",
 "dynamic_partition.prefix" = "p",
 "dynamic_partition.buckets" = "8",
 "dynamic_partition.start_day_of_month" = "1"
);