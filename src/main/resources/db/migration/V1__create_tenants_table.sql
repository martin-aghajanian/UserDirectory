CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    shard_key INTEGER NOT NULL CHECK (shard_key IN (0, 1)),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tenants_name UNIQUE (name)
);

CREATE INDEX idx_tenants_shard_key ON tenants(shard_key);