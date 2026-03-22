package com.martin.userdirectory.service;

import com.martin.userdirectory.dto.AnalyticsResponse;
import com.martin.userdirectory.dto.TenantAnalyticsReport;
import com.martin.userdirectory.entity.BillingStatus;
import com.martin.userdirectory.entity.Tenant;
import com.martin.userdirectory.repository.BillingRepository;
import com.martin.userdirectory.repository.TenantRepository;
import com.martin.userdirectory.repository.UserRepository;
import com.martin.userdirectory.sharding.DataSourceContext;
import com.martin.userdirectory.sharding.ShardRegistry;
import com.martin.userdirectory.sharding.ShardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final BillingRepository billingRepository;
    private final ShardingService shardingService;
    private final ShardRegistry shardRegistry;

    @Transactional(readOnly = true)
    public TenantAnalyticsReport generateTenantReport(Long tenantId) {
        log.info("Generating analytics report for tenant {} - routing to REPLICA", tenantId);

        int shardKey = shardRegistry.getShardKey(tenantId);
        DataSourceContext.setShardKey(shardKey);
        DataSourceContext.setReadOnly(true);

        try {
            String datasource = shardingService.resolveCurrentDataSource();
            log.info("Analytics query routed to datasource: {}", datasource);

            Tenant tenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));

            return buildTenantReport(tenant, datasource);
        } finally {
            DataSourceContext.clear();
        }
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse generateFullReport() {
        log.info("Generating full analytics report - routing to REPLICAS");

        List<TenantAnalyticsReport> reports = new ArrayList<>();

        // Iterate over all known shards and collect tenant reports
        for (int shard = 0; shard < shardRegistry.getShardCount(); shard++) {
            DataSourceContext.setShardKey(shard);
            DataSourceContext.setReadOnly(true);
            String datasource = shardingService.resolveCurrentDataSource();

            try {
                List<Tenant> tenants = tenantRepository.findAll();
                for (Tenant tenant : tenants) {
                    reports.add(buildTenantReport(tenant, datasource));
                }
            } finally {
                DataSourceContext.clear();
            }
        }

        return AnalyticsResponse.builder()
                .generatedAt(LocalDateTime.now())
                .routingStrategy("READ-ONLY transactions routed to REPLICA datasources")
                .datasourceType("REPLICA")
                .tenantReports(reports)
                .build();
    }

    private TenantAnalyticsReport buildTenantReport(Tenant tenant, String datasource) {
        Long tenantId = tenant.getId();

        Long userCount = (long) userRepository.findByTenantId(tenantId).size();
        Long billingCount = billingRepository.countByTenantId(tenantId);
        BigDecimal totalAmount = billingRepository.sumAmountByTenantId(tenantId);
        BigDecimal paidAmount = billingRepository.sumAmountByTenantIdAndStatus(tenantId, BillingStatus.PAID);
        BigDecimal pendingAmount = billingRepository.sumAmountByTenantIdAndStatus(tenantId, BillingStatus.PENDING);
        BigDecimal overdueAmount = billingRepository.sumAmountByTenantIdAndStatus(tenantId, BillingStatus.OVERDUE);

        return TenantAnalyticsReport.builder()
                .tenantId(tenantId)
                .tenantName(tenant.getName())
                .userCount(userCount)
                .billingRecordCount(billingCount)
                .totalBillingAmount(totalAmount)
                .paidAmount(paidAmount)
                .pendingAmount(pendingAmount)
                .overdueAmount(overdueAmount)
                .datasourceUsed(datasource)
                .routingInfo("READ-ONLY -> REPLICA")
                .build();
    }
}