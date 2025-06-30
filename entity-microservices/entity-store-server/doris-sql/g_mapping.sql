CREATE TABLE `g_mapping` (
  `data_time` datetime COMMENT '数据时间',
  `guid` varchar(40) COMMENT 'guid',
  `meta_data` varchar(200) COMMENT '数据所在位置',
  `status` smallint COMMENT '状态'
) ENGINE=olap
PARTITION BY RANGE(data_time) ()
DISTRIBUTED BY HASH(guid)
PROPERTIES
(
 "dynamic_partition.enable" = "true",
 "dynamic_partition.time_unit" = "MONTH",
 "dynamic_partition.end" = "2",
 "dynamic_partition.prefix" = "p",
 "dynamic_partition.buckets" = "8",
 "dynamic_partition.start_day_of_month" = "1"
);