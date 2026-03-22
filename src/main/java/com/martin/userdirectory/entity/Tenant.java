package com.martin.userdirectory.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "shard_key", nullable = false)
    private Integer shardKey;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Billing> billingRecords = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.setTenant(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setTenant(null);
    }

    public void addBillingRecord(Billing billing) {
        billingRecords.add(billing);
        billing.setTenant(this);
    }

    public void removeBillingRecord(Billing billing) {
        billingRecords.remove(billing);
        billing.setTenant(null);
    }
}