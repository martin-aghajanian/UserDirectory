package com.martin.userdirectory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantAnalyticsReport {

    private Long tenantId;
    private String tenantName;
    private Long userCount;
    private Long billingRecordCount;
    private BigDecimal totalBillingAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal overdueAmount;
    private String datasourceUsed;
    private String routingInfo;
}