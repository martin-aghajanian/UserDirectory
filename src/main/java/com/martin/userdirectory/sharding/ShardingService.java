package com.martin.userdirectory.sharding;

import org.springframework.stereotype.Service;

@Service
public class ShardingService {

    private static final int DEFAULT_SHARD = 0;

    public String resolveCurrentDataSource() {
        Integer shardKey = DataSourceContext.getShardKey();
        boolean isReadOnly = DataSourceContext.isReadOnly();

        int shard = (shardKey != null) ? shardKey : DEFAULT_SHARD;
        String suffix = isReadOnly ? "-replica" : "-primary";
        return "shard" + shard + suffix;
    }

    public String getShardName(Integer shardKey) {
        return "shard" + (shardKey != null ? shardKey : DEFAULT_SHARD);
    }
}