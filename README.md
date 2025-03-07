# Multi-Step Process with Spring Boot & Camunda

This branch implements a **dynamic, UI-driven multi-step process** using **Spring Boot** and **Camunda BPM**, 
allowing for configurable workflows and seamless state transitions. The backend utilizes **PostgreSQL with JSONB support** to efficiently store form data.

The project serves as a demonstration for the requirements outlined in my DZone article
[Dynamic Forms With Camunda and Spring StateMachine](https://dzone.com/articles/dynamic-forms-camunda-spring-statemachine).

ℹ️ Check other branches for variants.

---

## 🚀 Features

- **Dynamic Forms Definition**: Forms and fields per step are configurable via `application.yml`.
- **Camunda BPM**: Manages state transitions between steps.
- **PostgreSQL JSONB Storage**: Efficiently stores form data as structured JSON.
- **REST API**: Provides endpoints to interact with the process.
- **Docker Compose Support**: Easily spin up a Postgres database.
- **Basic UI for testing**: HTML/JavaScript page for testing via browser

---

## 🏗️ Project Structure

```
├── src/main/java/com/example/demolition # 😀
│   ├── config         # App configuration
│   ├── controller     # REST & MVC Controllers
│   ├── dto            
│   ├── entity         # Entity POJOs
│   ├── exception      
│   ├── repository     # JPA Repositories
│   ├── service        # Business Logic
│   ├── DemolitionApplication # Main Spring Boot App Entry
│
├── src/main/resources
│   ├── application.yml                # Configurable Process Forms Definitions
│   ├── loanApplicationProcess.bpmn    # Camunda process definition
│   ├── templates
│       ├── home.html      # Demo homepage 
│       ├── process.html   # Demo dynamic form rendering page     
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

The application uses **Camunda BPM** to basically manage step transitions.
Form rendering and submissions are relying on StateMachine state but are handled independently.

![BPMN Diagram](https://github.com/kmandalas/spring-statemachine-webapp/blob/camunda/diagram-1.png)


Application behavior (steps & forms) is based on `loanApplicationProcess.bpmn` and `application.yml`

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


