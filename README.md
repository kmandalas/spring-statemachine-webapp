# Multi-Step Process with Spring Boot & Spring State Machine

This project implements a **dynamic, UI-driven multi-step process** using **Spring Boot** and **Spring State Machine**, 
allowing for configurable workflows and seamless state transitions. 
The backend utilizes **PostgreSQL with JSONB support** to efficiently store form data.

---

## 🚀 Features

- **Dynamic Forms Definition**: Forms and fields per step are configurable via `application.yml`.
- **Spring State Machine**: Manages state transitions between steps.
- **PostgreSQL JSONB Storage**: Efficiently stores form data as structured JSON.
- **REST API**: Provides endpoints to interact with the process.
- **Docker Compose Support**: Easily spin up a Postgres database.
- **Basic UI for testing**: HTML/JavaScript page for testing via browser

---

## 🏗️ Project Structure

```
├── src/main/java/com/example/demolition # 😀
│   ├── config         # App & StateMachine configuration
│   ├── controller     # REST & MVC Controllers
│   ├── entity         # Entity POJOs
│   ├── repository     # JPA Repositories
│   ├── service        # Business Logic
│   ├── statemachine   # StateMachine Enums
│   ├── DemolitionApplication # Main Spring Boot App Entry
│
├── src/main/resources
│   ├── application.yml    # Configurable Process Forms Definitions
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
cd spring-statemachine-webapp
```

### Step 2: Run the Application

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

The application is based on **Spring State Machine** to basically manage step transitions.
Form rendering and submissions are relying on StateMachine state but are handled independently.

```
stateDiagram-v2
    [*] --> SELECTION
    SELECTION --> STEP_ONE: PROCESS_SELECTED
    STEP_ONE --> STEP_TWO: STEP_ONE_SUBMIT
    STEP_TWO --> STEP_THREE: STEP_TWO_SUBMIT
    STEP_THREE --> SUBMISSION: STEP_THREE_SUBMIT
    SUBMISSION --> [*]: FINAL_SUBMIT
    STEP_TWO --> STEP_ONE: BACK
    STEP_THREE --> STEP_TWO: BACK
    SUBMISSION --> STEP_THREE: BACK
```

![State Machine Diagram](https://github.com/kmandalas/spring-statemachine-webapp/blob/main/diagram-1.png)


States and transitions are configured at `StateMachineConfig.java`

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


