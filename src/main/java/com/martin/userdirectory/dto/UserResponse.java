package com.martin.userdirectory.dto;

import com.martin.userdirectory.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private Long tenantId;
    private String email;
    private String name;
    private String shard;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user, String shard) {
        return UserResponse.builder()
                .id(user.getId())
                .tenantId(user.getTenant().getId())
                .email(user.getEmail())
                .name(user.getName())
                .shard(shard)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
