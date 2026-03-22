package com.martin.userdirectory.controller;

import com.martin.userdirectory.dto.CreateTenantRequest;
import com.martin.userdirectory.dto.TenantResponse;
import com.martin.userdirectory.entity.Tenant;
import com.martin.userdirectory.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody CreateTenantRequest request) {
        Tenant tenant = tenantService.createTenant(request.getName());
        String shard = tenantService.getShardForTenant(tenant.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TenantResponse.fromEntity(tenant, shard));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable Long tenantId) {
        Tenant tenant = tenantService.getTenant(tenantId);
        String shard = tenantService.getShardForTenant(tenantId);
        return ResponseEntity.ok(TenantResponse.fromEntity(tenant, shard));
    }
}