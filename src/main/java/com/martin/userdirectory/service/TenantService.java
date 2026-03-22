package com.martin.userdirectory.service;

import com.martin.userdirectory.entity.Tenant;
import com.martin.userdirectory.repository.TenantRepository;
import com.martin.userdirectory.sharding.DataSourceContext;
import com.martin.userdirectory.sharding.ShardRegistry;
import com.martin.userdirectory.sharding.ShardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final ShardingService shardingService;
    private final ShardRegistry shardRegistry;

    private final AtomicInteger nextShard = new AtomicInteger(0);

    @Transactional
    public Tenant createTenant(String name) {
        // Assign shard via round-robin across available shards
        int shardKey = nextShard.getAndUpdate(n -> (n + 1) % shardRegistry.getShardCount());

        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(false);
        try {
            Tenant tenant = Tenant.builder()
                    .name(name)
                    .shardKey(shardKey)
                    .build();

            tenant = tenantRepository.save(tenant);

            // Register in the in-memory shard registry
            shardRegistry.register(tenant.getId(), shardKey);

            return tenant;
        } finally {
            DataSourceContext.clear();
        }
    }

    @Transactional(readOnly = true)
    public Tenant getTenant(Long tenantId) {
        int shardKey = shardRegistry.getShardKey(tenantId);
        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(false);
        try {
            return tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
        } finally {
            DataSourceContext.clear();
        }
    }

    public String getShardForTenant(Long tenantId) {
        int shardKey = shardRegistry.getShardKey(tenantId);
        return shardingService.getShardName(shardKey);
    }

    public String getDataSourceForTenant(Long tenantId, boolean readOnly) {
        int shardKey = shardRegistry.getShardKey(tenantId);
        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(readOnly);
        try {
            return shardingService.resolveCurrentDataSource();
        } finally {
            DataSourceContext.clear();
        }
    }
}