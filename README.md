# Dynamic Drools Rule-Engine — Spring Boot

A Spring Boot microservice that lets you:

* declare **new entities** and fields at runtime
* author **parameterised Drools rules** that live in PostgreSQL
* evaluate those rules through a simple REST API
* receive the mutated facts back as plain JSON

The engine converts each incoming JSON record into the generated `declare` POJO (if a schema exists), fires a **stateless** Drools session, and converts the result back to JSON so your client code never changes.

---

## Features
* **Runtime Schemas** &nbsp;`POST /schemas` stores a Drools `declare … end` block and immediately exposes the generated fact type.
* **Parameterised Rules** &nbsp;Tokens like `${TIME_MIN}` in the DRL are replaced with the values from a `params` JSON string that lives in the same DB row.
* **Stateless Evaluation** &nbsp;Each request gets its own `StatelessKieSession`; no side-effects, fully thread-safe.
* **Accumulate & Windows** &nbsp;Supports `count`, `sum`, sliding-time windows, and other Drools CEP features.
* **Loop-safe by default** &nbsp;Rules are stored with `no-loop true` so a single `modify` won’t cause an infinite refire.

---


* **RuleCompilerService** builds one `KieContainer` per `ruleSetId`, caching it for fast re-use.
* **RuleEngineService** hydrates JSON → POJO (if a schema exists), fires the stateless session, then converts POJO → JSON for the response.

---

## Tech Stack
| Area     | Library / Version | Notes |
|----------|-------------------|-------|
| Language | Java 21           | |
| Framework| Spring Boot 3.3   | |
| Rule Engine | Drools 8.x      | Dynamic DRL loading |
| Database | PostgreSQL 15     | Stores rules & schemas |
| Build    | Maven 3.9         | |
| Docker   | Compose file included | for local dev |

---

## Quick Start
1. **Clone & run**

   ```bash
   git clone https://github.com/your-org/dynamic-rule-engine.git
   cd dynamic-rule-engine
   ./mvnw spring-boot:run

