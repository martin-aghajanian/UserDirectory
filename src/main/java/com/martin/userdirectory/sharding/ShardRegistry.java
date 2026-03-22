package com.martin.userdirectory.sharding;

import com.martin.userdirectory.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShardRegistry {

    private static final int SHARD_COUNT = 2;

    private final ConcurrentHashMap<Long, Integer> tenantShardMap = new ConcurrentHashMap<>();
    private final TenantRepository tenantRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadMappings() {
        log.info("Loading tenant-to-shard mappings from all shards...");
        for (int shard = 0; shard < SHARD_COUNT; shard++) {
            DataSourceContext.setShardKey(shard);
            DataSourceContext.setReadOnly(false);
            try {
                tenantRepository.findAll().forEach(t -> {
                    tenantShardMap.put(t.getId(), t.getShardKey());
                    log.info("Registered tenant {} -> shard {}", t.getId(), t.getShardKey());
                });
            } catch (Exception e) {
                log.warn("Could not load tenants from shard {} - schema may not be initialized: {}", shard, e.getMessage());
            } finally {
                DataSourceContext.clear();
            }
        }
        log.info("Loaded {} tenant-shard mappings", tenantShardMap.size());
    }

    public void register(Long tenantId, Integer shardKey) {
        tenantShardMap.put(tenantId, shardKey);
        log.info("Registered tenant {} -> shard {}", tenantId, shardKey);
    }

    public Integer getShardKey(Long tenantId) {
        Integer key = tenantShardMap.get(tenantId);
        if (key == null) {
            throw new RuntimeException("No shard mapping found for tenant: " + tenantId);
        }
        return key;
    }

    public Set<Integer> getAllShards() {
        if (tenantShardMap.isEmpty()) {
            return Set.of(0);
        }
        return tenantShardMap.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public Map<Long, Integer> getAllMappings() {
        return Collections.unmodifiableMap(tenantShardMap);
    }

    public int getShardCount() {
        return SHARD_COUNT;
    }
}