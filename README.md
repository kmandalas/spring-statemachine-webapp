# Multi-Step Process with Spring Boot & Spring State Machine

This project implements a **dynamic, UI-driven multi-step process** using **Spring Boot** and **Spring State Machine**, allowing for configurable workflows and seamless state transitions. The backend utilizes **PostgreSQL with JSONB support** to efficiently store form data.

---

## 🚀 Features

- **Dynamic Process Definition**: Steps and fields are configurable via `application.yml`.
- **Spring State Machine**: Manages state transitions between steps.
- **PostgreSQL JSONB Storage**: Efficiently stores form data as structured JSON.
- **REST API**: Provides endpoints to interact with the process.
- **Docker Compose Support**: Easily spin up a Postgres database.

---

## 🏗️ Project Structure

```
├── src/main/java/com/example/process
│   ├── controller         # REST Controllers
│   ├── service            # Business Logic & State Handling
│   ├── repository         # JPA Repositories
│   ├── model              # Entities & Data Models
│   ├── statemachine       # State Machine Configurations
│   ├── config             # App Configurations (YAML Parsing, etc.)
│   ├── ProcessApplication # Main Spring Boot App Entry
│
├── src/main/resources
│   ├── application.yml    # Configurable Process Definitions
│   ├── db/migration       # Database Migrations (Flyway)
│
├── docker-compose.yml     # Docker Compose for PostgreSQL
├── README.md              # Project Documentation
```

---

## 🛠️ Setup & Installation

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

### Step 1: Clone the Repository

```sh
git clone https://github.com/kmandalas/spring-statemachine-webapp.git
cd spring-statemachine-webapp
```

### Step 2: Start PostgreSQL with Docker

```sh
docker-compose up -d
```

This will start a PostgreSQL database instance.

### Step 3: Run the Application

```sh
mvn spring-boot:run
```

Application will be available at `http://localhost:8080`

---

## 📝 API Endpoints

### Start a New Process

```http
POST /api/process/start
Content-Type: application/json
{
  "processType": "loan_application"
}
```

### Submit a Step

```http
POST /api/process/{processId}/submit
Content-Type: application/json
{
  "step": "step_one",
  "formData": { "firstName": "John", "lastName": "Doe" }
}
```

### Get Process Summary

```http
GET /api/process/{processId}/summary
```

---

## 🔄 State Machine Diagram

The application uses **Spring State Machine** to manage step transitions dynamically.

```
[START] → Step One → Step Two → Step Three → Submission → [END]
```

State transitions are controlled based on `application.yml`.

---

## 📌 Configuration Example (`application.yml`)

```yaml
form:
  processes:
    loan_application:
      name: Loan Application
      steps:
        step_one:
          title: "Personal Information"
          fields:
            - id: "firstName"
              label: "First Name"
              type: "text"
              required: true
```

---

## 🛠️ Tech Stack

- **Spring Boot 3**
- **Spring State Machine**
- **Spring Data JPA** (PostgreSQL JSONB support)
- **Docker & Docker Compose**
- **Jackson** (JSON Parsing)

---

## 🤝 Contributing

Feel free to submit issues or pull requests! 🚀

---


