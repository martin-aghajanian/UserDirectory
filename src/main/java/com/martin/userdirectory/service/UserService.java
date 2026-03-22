package com.martin.userdirectory.service;

import com.martin.userdirectory.dto.CreateUserRequest;
import com.martin.userdirectory.dto.UserResponse;
import com.martin.userdirectory.entity.Tenant;
import com.martin.userdirectory.entity.User;
import com.martin.userdirectory.repository.TenantRepository;
import com.martin.userdirectory.repository.UserRepository;
import com.martin.userdirectory.sharding.DataSourceContext;
import com.martin.userdirectory.sharding.ShardRegistry;
import com.martin.userdirectory.sharding.ShardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ShardingService shardingService;
    private final ShardRegistry shardRegistry;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        Long tenantId = request.getTenantId();
        int shardKey = shardRegistry.getShardKey(tenantId);

        log.info("Creating user for tenant {} on shard{} (primary)", tenantId, shardKey);

        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(false);
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));

            User user = User.builder()
                    .tenant(tenant)
                    .email(request.getEmail())
                    .name(request.getName())
                    .build();

            user = userRepository.save(user);
            String datasource = shardingService.resolveCurrentDataSource();

            log.info("Created user {} on datasource {}", user.getId(), datasource);

            return UserResponse.fromEntity(user, datasource);
        } finally {
            DataSourceContext.clear();
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long tenantId, Long userId) {
        int shardKey = shardRegistry.getShardKey(tenantId);

        log.info("Fetching user {} for tenant {} from shard{}", userId, tenantId, shardKey);

        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(false);
        try {
            User user = userRepository.findByIdAndTenantId(userId, tenantId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            String datasource = shardingService.resolveCurrentDataSource();
            return UserResponse.fromEntity(user, datasource);
        } finally {
            DataSourceContext.clear();
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByTenant(Long tenantId) {
        int shardKey = shardRegistry.getShardKey(tenantId);

        log.info("Fetching all users for tenant {} from shard{}", tenantId, shardKey);

        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(false);
        try {
            String datasource = shardingService.resolveCurrentDataSource();
            return userRepository.findByTenantId(tenantId).stream()
                    .map(user -> UserResponse.fromEntity(user, datasource))
                    .toList();
        } finally {
            DataSourceContext.clear();
        }
    }
}