package com.martin.userdirectory.config;

import com.martin.userdirectory.sharding.ShardRoutingDataSource;
import com.martin.userdirectory.sharding.ShardingService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    // Primary datasources for writes
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard0-primary")
    public DataSource shard0PrimaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard1-primary")
    public DataSource shard1PrimaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    // Replica datasources for read-only analytics
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard0-replica")
    public DataSource shard0ReplicaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard1-replica")
    public DataSource shard1ReplicaDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("shard0PrimaryDataSource") DataSource shard0Primary,
            @Qualifier("shard1PrimaryDataSource") DataSource shard1Primary,
            @Qualifier("shard0ReplicaDataSource") DataSource shard0Replica,
            @Qualifier("shard1ReplicaDataSource") DataSource shard1Replica,
            ShardingService shardingService) {

        ShardRoutingDataSource routingDataSource = new ShardRoutingDataSource(shardingService);

        Map<Object, Object> targetDataSources = new HashMap<>();

        // Primary datasources for write operations
        targetDataSources.put("shard0-primary", shard0Primary);
        targetDataSources.put("shard1-primary", shard1Primary);

        // Replica datasources for read-only operations
        targetDataSources.put("shard0-replica", shard0Replica);
        targetDataSources.put("shard1-replica", shard1Replica);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(shard0Primary);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}