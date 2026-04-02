paymentGateway: High-Throughput Async Payment Engine
paymentGateway is a robust, event-driven backend engine built with Spring Boot. It is designed to handle high volumes of payment initiations with a focus on financial accuracy, idempotency, and a reliable audit trail.
🚀 Core Features
Asynchronous Ingestion: Immediate 202 Accepted response with a tracking ID to ensure high throughput.
Financial Accuracy: Built-in Idempotency (preventing duplicate charges) and Optimistic Locking (preventing race conditions).
Event-Driven Pipeline: Uses Spring Application Events to decouple Ingestion, Fraud Assessment, and Bank Communication.
Resilient Design: Includes a Bank Simulator with variable latency and a Retry Mechanism with exponential backoff.
Compliance Ready: An immutable, append-only Audit Log tracks every state change from INITIALIZED to COMPLETED/FAILED.
🏗️ Architecture & Workflow
POST /v1/payments: Validates the request and checks for an existing idempotencyKey.
Internal Event: If new, it persists the payment and broadcasts a PaymentIngestedEvent.
Async Pipeline:
Fraud Check: A pluggable service validates the transaction security.
Acquiring Bank: A simulated external call handles authorization (simulating network flakiness).
Audit Trail: Every step triggers a Propagation.REQUIRES_NEW transaction to record the transition, ensuring history is kept even if the main process fails.
🛠️ Technical Stack
Framework: Spring Boot 3.x
Persistence: Spring Data JPA (Hibernate)
Database: H2 (Development) / PostgreSQL (Production ready)
Utilities: Lombok, Spring Retry, Jakarta Validation
🚦 API Endpoints
1. Initiate Payment
POST /v1/payments
json
{
  "amount": 150.00,
  "currency": "USD",
  "idempotencyKey": "order-ref-12345"
}
Use code with caution.

Response: 202 Accepted
json
{
  "trackingId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "ACCEPTED",
  "timestamp": "2023-10-27T10:00:00Z"
}
Use code with caution.

2. Check Status
GET /v1/payments/{trackingId}
Response: 200 OK
json
{
  "trackingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "statusDescription": "Payment successfully captured.",
  "amount": 150.00,
  "currency": "USD"
}
Use code with caution.

🧪 Running Locally
Clone the repo: git clone https://github.com
Build: ./mvnw clean install
Run: ./mvnw spring-boot:run
🛡️ Critical Design Decisions
Optimistic Locking (@Version): Essential for the gateway to handle a "Late Bank Callback" and a "System Timeout" simultaneously without corrupting the final status.
Idempotency Key: Enforced at the Database level via a unique index to ensure that retries from the client never result in duplicate financial records.
Async Thread Pool: Configured to isolate payment processing from the web server threads, preventing the API from becoming unresponsive during bank outages.
