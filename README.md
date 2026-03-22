# User Directory

Multi-tenant User Directory with application-level sharding using Spring Boot and PostgreSQL.

## Setup

```bash
docker-compose up -d
./mvnw spring-boot:run
```

App runs on `http://localhost:8080`.

## Endpoints

- `POST /api/tenants` — `{ "name": "Acme Corp" }`
- `GET /api/tenants/{tenantId}`
- `POST /api/tenants/{tenantId}/users` — `{ "email": "john@acme.com", "name": "John Doe" }`
- `GET /api/tenants/{tenantId}/users`
- `GET /api/tenants/{tenantId}/users/{userId}`
- `GET /api/analytics/report`
- `GET /api/analytics/report/tenant/{tenantId}`