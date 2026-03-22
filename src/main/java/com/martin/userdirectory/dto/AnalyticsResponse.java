package com.martin.userdirectory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private LocalDateTime generatedAt;
    private String routingStrategy;
    private String datasourceType;
    private List<TenantAnalyticsReport> tenantReports;
}