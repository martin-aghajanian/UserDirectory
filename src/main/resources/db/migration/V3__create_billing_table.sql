CREATE TABLE billing (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(50) NOT NULL,
    billing_date DATE NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billing_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT chk_billing_status CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED'))
);

CREATE INDEX idx_billing_tenant_id ON billing(tenant_id);
CREATE INDEX idx_billing_status ON billing(status);
CREATE INDEX idx_billing_date ON billing(billing_date);