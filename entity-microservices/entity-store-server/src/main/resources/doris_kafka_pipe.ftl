    CREATE ROUTINE LOAD ${pipe.name}Job on ${pipe.tbl}
        PROPERTIES
        (
            "desired_concurrent_number"="1",
            "strict_mode"="false",
            "format" = "json"
        )
        FROM KAFKA
        (
            "kafka_broker_list"= "${pipe.brokerList}",
            "kafka_topic" = "${pipe.topic}",
            "property.group.id" = "${pipe.groupId}",
            "property.kafka_default_offsets" = "OFFSET_BEGINNING",
            "property.enable.auto.commit" = "false"
        );