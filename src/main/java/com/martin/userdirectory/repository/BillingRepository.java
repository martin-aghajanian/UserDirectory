package com.martin.userdirectory.repository;

import com.martin.userdirectory.entity.Billing;
import com.martin.userdirectory.entity.BillingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    List<Billing> findByTenantId(Long tenantId);

    List<Billing> findByTenantIdAndStatus(Long tenantId, BillingStatus status);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Billing b WHERE b.tenant.id = :tenantId")
    BigDecimal sumAmountByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Billing b WHERE b.tenant.id = :tenantId AND b.status = :status")
    BigDecimal sumAmountByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") BillingStatus status);

    @Query("SELECT COUNT(b) FROM Billing b WHERE b.tenant.id = :tenantId")
    Long countByTenantId(@Param("tenantId") Long tenantId);
}