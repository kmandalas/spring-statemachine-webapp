# Multi-Step Process with Spring Boot & Camunda

This project implements a **dynamic, UI-driven multi-step process** using **Spring Boot** and **Camunda BPM**, allowing for configurable workflows and seamless state transitions. The backend utilizes **PostgreSQL with JSONB support** to efficiently store form data.

---

## 🚀 Features

- **Forms Definition**: Forms and fields per step are configurable via `application.yml`.
- **Camunda BPM**: Manages the process flow and transitions.
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
│   ├── application.yml    # Configurable Form Definitions
│   |── loanApplicationProcess.bpmn    # Camunda process definition
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
git checkout camunda
cd spring-statemachine-webapp
```

### Step 2: Run the Application

```sh
mvn spring-boot:run
```

Application will be available at `http://localhost:8080`

Camunda cockpit is accesible at `http://localhost:8081/camunda`

---

## 🔄 BPMN Diagram

The application uses **Camunda BPM** to manage step transitions.

![BPMN Diagram](https://github.com/kmandalas/spring-statemachine-webapp/blob/main/diagram-1.png)


Application behavior (steps & forms) are based on `application.yml` and `loanApplicationProcess.bpmn`

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
- **Camunda BPM**
- **Spring Data JPA** (PostgreSQL JSONB support)
- **Docker & Docker Compose**
- **Jackson** (JSON Parsing)

---

## 🤝 Contributing

Feel free to submit issues or pull requests! 🚀

---


