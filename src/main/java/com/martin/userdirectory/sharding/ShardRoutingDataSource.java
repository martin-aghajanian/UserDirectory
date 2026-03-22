package com.martin.userdirectory.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class ShardRoutingDataSource extends AbstractRoutingDataSource {

    private final ShardingService shardingService;

    public ShardRoutingDataSource(ShardingService shardingService) {
        this.shardingService = shardingService;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String key = shardingService.resolveCurrentDataSource();
        log.debug("Routing to datasource: {}", key);
        return key;
    }
}