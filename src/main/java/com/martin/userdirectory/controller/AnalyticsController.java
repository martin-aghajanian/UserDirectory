package com.martin.userdirectory.controller;

import com.martin.userdirectory.dto.AnalyticsResponse;
import com.martin.userdirectory.dto.TenantAnalyticsReport;
import com.martin.userdirectory.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Generate full analytics report across all tenants.
     * This heavy read operation is routed to REPLICA datasources.
     */
    @GetMapping("/report")
    public ResponseEntity<AnalyticsResponse> getFullReport() {
        AnalyticsResponse report = analyticsService.generateFullReport();
        return ResponseEntity.ok(report);
    }

    /**
     * Generate analytics report for a specific tenant.
     * This heavy read operation is routed to the tenant's REPLICA datasource.
     */
    @GetMapping("/report/tenant/{tenantId}")
    public ResponseEntity<TenantAnalyticsReport> getTenantReport(@PathVariable Long tenantId) {
        TenantAnalyticsReport report = analyticsService.generateTenantReport(tenantId);
        return ResponseEntity.ok(report);
    }
}