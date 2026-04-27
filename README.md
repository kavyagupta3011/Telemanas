# Telemanas Real-Time Monitoring System

## Overview
A near real-time monitoring and analytics system for tele-mental health operations.  
Processes high-volume call center events and provides real-time visibility into system performance.

## Architecture
External APIs / Simulator → Producer Service → Kafka → Consumer Service → PostgreSQL → Grafana

## Tech Stack
- Java, Spring Boot
- Apache Kafka
- PostgreSQL
- Grafana
- Docker, Docker Compose

## Key Features
- Event-driven architecture
- Real-time event ingestion and processing
- Kafka-based decoupled pipeline
- Persistent storage using PostgreSQL
- Grafana dashboards for monitoring

## Kafka Topics
- user-session-events
- agent-activity-events
- agent-autocall-events
- live-call-events
- cm-cdr-events
- user-disposition-events

## Core Event Flows
| Domain            | Topic                   | Key        | Table                    |
|------------------|------------------------|------------|--------------------------|
| User Session     | user-session-events     | sessionId  | user_sessions            |
| Agent Activity   | agent-activity-events   | sessionId  | agent_activity           |
| Auto Call        | agent-autocall-events   | sessionId  | autocall_activity        |
| Live Calls       | live-call-events        | callId     | live_calls               |
| CM CDR           | cm-cdr-events           | callLegId  | cm_cdr_history           |
| User Disposition | user-disposition-events | id         | user_disposition_history |

## Setup

### 1. Clone
```bash
git clone https://github.com/kavyagupta3011/Telemanas
cd Telemanas
```

### 2. Start Infrastructure
```bash
cd telemanas_docker
docker compose up -d
```

### 3. Run Services

Start consumer first:
```bash
cd event-consumer
mvn spring-boot:run
```

Then producer:
```bash
cd event-producer
mvn spring-boot:run
```

## Ports
- Kafka: 9092
- PostgreSQL: 5433
- Grafana: 3000

## Database
- Name: metricsdb

## API Endpoints
- /api/user-sessions
- /api/agent-activity
- /api/auto-call
- /api/live-calls
- /api/cm-cdr
- /api/user-disposition

## Notes
- Simulator runs by when enabled in application.yaml of event producer 
- Kafka uses key-based partitioning to maintain ordering

## Future Improvements
- Multi-broker Kafka cluster
- Horizontal scaling of consumers
- Database indexing and read replicas
- Enhanced Grafana dashboards
