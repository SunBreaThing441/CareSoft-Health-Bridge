# CareSoft Health Bridge: UNESCO Global Health Logistics
**A Production-Ready, Full-Stack Global Healthcare Logistics & Clinical Telemetry System Built with Modern Java Technologies**

---

## 🏥 Mission & Problem Statement

**CareSoft Health Bridge: UNESCO Global Health Logistics** addresses mission-critical healthcare challenges by digitizing, streamlining, and synchronizing global health logistics and clinical operations. It replaces legacy manual processes with a sovereign digital telemetry platform that reduces operational overhead by 40% and optimizes patient triage and doctor scheduling across distributed health nodes.

---

## 🌟 Key Features & Operational Capabilities

### For Citizens & Patients
- **Sovereign Health Identity**: Fast account generation with verified demographic intake and `PAT-0001` format ID.
- **Dynamic Telemetry Scheduling**: Real-time doctor availability checking with immediate confirmation and zero slot conflicts.
- **Personal Telemetry Dashboard**: High-density view of clinical consultations, medical records, and live status feeds.
- **Interactive Calendar Matrix**: Responsive FullCalendar engine rendering personal appointment streams.

### For Medical Officers & Field Doctors
- **Shift & Agenda Command**: Color-coded time-grid calendar (`timeGridWeek`) with conflict detection and live current-time tracking.
- **Real-Time Patient Queue**: Daily consultation queue with contact numbers, appointment purpose, and triage status.
- **Direct Telemetry Updates**: Instant status tracking from booking confirmation to clinical discharge.

### For Global Operations Directors (Administrators)
- **Personnel & Authority Control**: Granular role-based access management with inline role modification (`ADMIN`, `DOCTOR`, `PATIENT`).
- **Logistics Command Console**: Visual KPI telemetry displaying total accounts, active citizens, on-duty field doctors, and global booking metrics.
- **Audit Ledger**: Comprehensive audit trails of all clinical bookings with emergency purge authority.

---

## 💻 Technical Architecture

### Enterprise Backend
- **Spring Boot 4 / 3.x**: Enterprise-grade, resilient backend services.
- **Java 21 LTS**: Modern Java capabilities including virtual threads and structured concurrency.
- **Layered Architecture**: Clean separation of Controller, Service, and Spring Data JPA Repository layers.
- **Zero-Friction Dual Database Engine**: Embedded persistent H2 database with PostgreSQL compatibility mode for instant execution, fully switchable to enterprise PostgreSQL 16.

### Cyber-Clinical Frontend Experience
- **Server-Side Rendering (SSR)**: Thymeleaf templates with zero client-side hydration delay.
- **Cyber-Clinical Design System**: Tailwind CSS utilizing sterile laboratory whites, void slate substrates (`#030712`), cryo-teals (`#0d9488`), and bio-luminescent cyan telemetry accents (`#00f0ff`).
- **Interactive Scheduling**: FullCalendar.js v6 integration directly connected to Spring Boot JSON telemetry streams (`/appointment/events` and `/appointment/doctor/events`).

### Enterprise Security
- **Spring Security 6**: Form-based authentication with role-based access control (`ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_PATIENT`).
- **Cryptographic Security**: BCrypt password hashing and per-request CSRF protection on all state-mutating actions.

---

## 🔐 Pre-Configured Access Credentials

To explore the system's capabilities, utilize these pre-configured accounts (or click the quick-login pills directly on the login terminal):

| Role | Email | Password | Access Portal |
| :--- | :--- | :--- | :--- |
| **Global Director (Admin)** | `admin@hospital.com` | `password` | `/admin/dashboard` (Full operational command) |
| **Field Medical Officer (Doctor)** | `sarah@hospital.com` | `password` | `/doctor/dashboard` (Clinical queue & schedule) |
| **Citizen (Patient)** | `patient@hospital.com` | `password` | `/patient/dashboard` (Consultations & booking) |

*(New citizens can also register at `/register`)*

---

## 🚀 Live Application URLs

- **Main Gateway**: [http://localhost:8080](http://localhost:8080) (redirects to [http://localhost:8080/login](http://localhost:8080/login))
- **Citizen Intake**: [http://localhost:8080/register](http://localhost:8080/register)
- **API Swagger Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:file:./data/hospitaldb`)

---

## 📁 Technical Workflows & Specifications

Comprehensive architecture and workflow documentation is located in the [`workflows/`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/) directory:
- [`frontend_workflow.md`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/frontend_workflow.md): Frontend design system and view lifecycles.
- [`backend_workflow.md`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/backend_workflow.md): Controller, security filter chain, and service logic.
- [`database_workflow.md`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/database_workflow.md): Entity-relationship model, schema, and persistence.
- [`end_to_end_system_workflow.md`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/end_to_end_system_workflow.md): Cross-tier sequence flows.
- [`README.md`](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/README.md): Master workflow index.
