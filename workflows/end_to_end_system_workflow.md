# 🔄 End-to-End System Architecture & Workflow

This document provides a holistic blueprint of how the **Frontend Tier**, **Backend Tier**, and **Database Tier** coordinate in **CareSoft Health Bridge: UNESCO Global Health Logistics**.

---

## 1. System Topology & Tier Architecture

```mermaid
flowchart TB
    subgraph ClientTier ["1. Presentation Tier (Thymeleaf + Tailwind + FullCalendar)"]
        Browser["User Web Browser"]
        LoginView["/login, /register (Auth Views)"]
        PatientPortal["/patient/dashboard (Health Calendar)"]
        DoctorPortal["/doctor/dashboard (Daily Agenda)"]
        AdminPortal["/admin/dashboard (Hospital Operations)"]
    end

    subgraph SecurityTier ["2. Security & Routing Layer (Spring Security 6)"]
        FilterChain["Security Filter Chain (CSRF, Sessions)"]
        AuthManager["Authentication Manager & BCrypt"]
        SmartRouter["/dashboard Dynamic Role Router"]
    end

    subgraph BackendTier ["3. Backend Business Engine (Spring Boot 3/4)"]
        direction TB
        Controllers["Controllers<br/>• HomeController<br/>• PatientController<br/>• DoctorController<br/>• AppointmentController<br/>• AdminController"]
        Services["Services<br/>• PatientService"]
        Repositories["Spring Data JPA Repositories<br/>• UserRepository<br/>• PatientRepository<br/>• AppointmentRepository"]
    end

    subgraph DatabaseTier ["4. Persistent Storage Tier"]
        DBEngine[("H2 Persistent Database Engine<br/>./data/hospitaldb.mv.db<br/>(PostgreSQL Dialect Compatible)")]
    end

    Browser --> LoginView & PatientPortal & DoctorPortal & AdminPortal
    LoginView & PatientPortal & DoctorPortal & AdminPortal --> FilterChain
    FilterChain --> AuthManager
    AuthManager --> SmartRouter
    SmartRouter --> Controllers
    Controllers --> Services
    Services --> Repositories
    Controllers --> Repositories
    Repositories --> DBEngine
```

---

## 2. Core End-to-End Sequence Workflows

### 2.1 Complete Patient Lifecycle (Registration to Appointment)

```mermaid
sequenceDiagram
    autonumber
    actor Patient as Patient
    participant UI as Frontend (Thymeleaf/Tailwind)
    participant Sec as Spring Security
    participant Ctrl as Spring Controllers
    participant Svc as PatientService
    participant Repo as Repositories
    participant DB as Database (H2/Postgres)

    Note over Patient,DB: Phase 1: Registration
    Patient->>UI: Fill registration form on /register
    UI->>Sec: POST /register
    Sec->>Ctrl: PatientController.registerPatient(...)
    Ctrl->>Svc: registerPatient(patient, password)
    Svc->>Repo: userRepo.save(user) + patientRepo.save(patient)
    Repo->>DB: INSERT INTO users & patients
    DB-->>Repo: Saved
    Ctrl-->>UI: 302 Redirect /login (with flash success msg)

    Note over Patient,DB: Phase 2: Login & Role Delegation
    Patient->>UI: Submit credentials on /login
    UI->>Sec: POST /login (username, password, _csrf)
    Sec->>DB: Verify BCrypt hash against users table
    DB-->>Sec: Credentials Valid (ROLE_PATIENT)
    Sec-->>UI: 302 Redirect /dashboard
    UI->>Ctrl: GET /dashboard
    Ctrl-->>UI: 302 Redirect /patient/dashboard

    Note over Patient,DB: Phase 3: Appointment Booking
    Patient->>UI: Click "Book Appointment"
    UI->>Ctrl: GET /appointment/book
    Ctrl->>Repo: userRepo.findAll() by Role.DOCTOR
    Repo->>DB: SELECT * FROM users WHERE role='DOCTOR'
    DB-->>Repo: Doctor list
    Ctrl-->>UI: Render book.html with doctor select dropdown
    Patient->>UI: Select Dr. Sarah Jenkins, date & time, submit
    UI->>Ctrl: POST /appointment/book
    Ctrl->>Repo: appointmentRepo.save(appointment)
    Repo->>DB: INSERT INTO appointments
    DB-->>Repo: Persisted
    Ctrl-->>UI: Redirect to /appointment/calendar
    UI->>Ctrl: GET /appointment/events (AJAX)
    Ctrl->>Repo: findByPatientUserIdOrderByStartTimeAsc(...)
    Repo-->>Ctrl: Appointment List
    Ctrl-->>UI: JSON Event Feed
    UI-->>Patient: Render Interactive FullCalendar with booking badge
```

---

### 2.2 Doctor Daily Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Doctor as Dr. Sarah Jenkins
    participant UI as Doctor Portal UI
    participant Ctrl as DoctorController
    participant ACtrl as AppointmentController
    participant Repo as AppointmentRepository
    participant DB as Database

    Doctor->>UI: Log in with sarah@hospital.com
    UI->>Ctrl: GET /doctor/dashboard
    Ctrl->>Repo: findByDoctorIdAndStartDate(doctorId, today)
    Repo->>DB: Range query on start_time between today 00:00 and 23:59
    DB-->>Repo: Today's queue
    Ctrl->>Repo: findByDoctorIdAndStartTimeAfter(doctorId, now)
    Repo-->>Ctrl: Upcoming appointments
    Ctrl-->>UI: Render dashboard.html with today's patient queue
    UI->>ACtrl: GET /appointment/doctor/events (FullCalendar AJAX)
    ACtrl->>Repo: findByDoctorIdOrderByStartTimeAsc(doctorId)
    Repo-->>ACtrl: List of doctor's appointments
    ACtrl-->>UI: Sanitized JSON events (start, end, title, patientName)
    UI-->>Doctor: Display interactive weekly calendar agenda
```

---

### 2.3 Administrator Oversight & Governance

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Hospital Administrator
    participant UI as Admin Dashboard UI
    participant Ctrl as AdminController
    participant Repo as Repositories
    participant DB as Database

    Admin->>UI: Access /admin/dashboard
    UI->>Ctrl: GET /admin/dashboard
    Ctrl->>Repo: count() for Users, Patients, Appointments, Doctors
    Repo->>DB: SELECT COUNT(*) ...
    DB-->>Repo: Aggregated metrics
    Ctrl->>Repo: findTop10ByOrderByStartTimeDesc()
    Repo-->>Ctrl: Recent appointment records
    Ctrl-->>UI: Render admin/dashboard.html with metric cards & activity table

    Note over Admin,DB: User Role Management
    Admin->>UI: Navigate to /admin/users
    UI->>Ctrl: GET /admin/users
    Ctrl->>Repo: userRepo.findAll()
    Repo-->>Ctrl: User table
    Ctrl-->>UI: Render users.html
    Admin->>UI: Change user role or click Delete
    UI->>Ctrl: POST /admin/users/{id}/role or /delete
    Ctrl->>Repo: Update role / Delete record
    Repo->>DB: UPDATE / DELETE in users
    DB-->>Repo: Committed
    Ctrl-->>UI: 302 Redirect /admin/users (with confirmation alert)
```

---

## 3. System Verification & Live Access Points

| Component | URL Endpoint | Auth Requirement | Description |
| :--- | :--- | :--- | :--- |
| **Landing & Login** | [http://localhost:8080/login](http://localhost:8080/login) | Public | Application gateway and login form |
| **Self-Registration** | [http://localhost:8080/register](http://localhost:8080/register) | Public | New patient signup |
| **Smart Router** | [http://localhost:8080/dashboard](http://localhost:8080/dashboard) | Authenticated | Dispatches user to their respective portal |
| **Patient Portal** | [http://localhost:8080/patient/dashboard](http://localhost:8080/patient/dashboard) | `ROLE_PATIENT` | Metric summary & patient appointment calendar |
| **Appointment Booking** | [http://localhost:8080/appointment/book](http://localhost:8080/appointment/book) | `ROLE_PATIENT` | Doctor selection & timeslot scheduler |
| **Doctor Portal** | [http://localhost:8080/doctor/dashboard](http://localhost:8080/doctor/dashboard) | `ROLE_DOCTOR` | Daily queue and weekly agenda |
| **Admin Dashboard** | [http://localhost:8080/admin/dashboard](http://localhost:8080/admin/dashboard) | `ROLE_ADMIN` | Global KPIs and recent booking log |
| **Admin User Control** | [http://localhost:8080/admin/users](http://localhost:8080/admin/users) | `ROLE_ADMIN` | Role modification and account deletion |
| **API Documentation** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Public | OpenAPI 3.0 interactive API documentation |
| **Database Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | Public | Web SQL explorer for inspecting tables |
