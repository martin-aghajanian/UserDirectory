package com.martin.userdirectory.sharding;

public class DataSourceContext {

    public enum DataSourceType {
        PRIMARY,
        REPLICA
    }

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURRENT_SHARD = new ThreadLocal<>();
    private static final ThreadLocal<DataSourceType> CURRENT_TYPE = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void setShardKey(Integer shardKey) {
        CURRENT_SHARD.set(shardKey);
    }

    public static Integer getShardKey() {
        return CURRENT_SHARD.get();
    }

    public static void setReadOnly(boolean readOnly) {
        CURRENT_TYPE.set(readOnly ? DataSourceType.REPLICA : DataSourceType.PRIMARY);
    }

    public static DataSourceType getDataSourceType() {
        DataSourceType type = CURRENT_TYPE.get();
        return type != null ? type : DataSourceType.PRIMARY;
    }

    public static boolean isReadOnly() {
        return DataSourceType.REPLICA.equals(CURRENT_TYPE.get());
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_SHARD.remove();
        CURRENT_TYPE.remove();
    }
}