package com.martin.userdirectory.dto;

import com.martin.userdirectory.entity.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponse {

    private Long id;
    private String name;
    private Integer shardKey;
    private String shard;

    public static TenantResponse fromEntity(Tenant tenant, String shard) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .shardKey(tenant.getShardKey())
                .shard(shard)
                .build();
    }
}