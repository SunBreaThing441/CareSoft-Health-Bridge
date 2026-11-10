# CareSoft Health Bridge: UNESCO Global Health Logistics

> **CareSoft Health Bridge** is a cyber-clinical, enterprise-grade healthcare logistics and hospital management platform operating under the technical telemetry standards of the **UNESCO Global Health Initiative**.

---

## 🌐 System Architecture & Technology Stack

CareSoft Health Bridge features a decoupled presentation architecture backed by an enterprise Spring Boot framework:

### Backend Services
- **Framework**: Spring Boot 3.x / Java 17
- **Security**: Spring Security 6 (Stateless CSRF with `X-CSRF-TOKEN` rotation, BCrypt credential hashing, Role-Based Access Control)
- **Persistence**: Spring Data JPA, Hibernate ORM
- **Database**: H2 Embedded Engine (`file:./data/hospitaldb;MODE=PostgreSQL`) with automated database migration and PostgreSQL production readiness

### Frontend Presentation Tier
- **Design System**: Cyber-Clinical Aesthetic (Void Slate `#030712`, Cryo-Teal `#0d9488`, Bio-Luminescent Cyan `#00f0ff`, Glassmorphism)
- **Interactive Agendas**: FullCalendar.js v6 (Decoupled JSON telemetry streams from `/appointment/events` and `/appointment/doctor/events`)
- **Security Guard Micro-Engine**: Client-side fetch interceptor dynamically extracting and applying `X-CSRF-TOKEN` headers
- **Styling**: Tailwind CSS, JetBrains Mono & Plus Jakarta Sans typography, Font Awesome 6 Icons

---

## 👥 Pre-Configured Test Credentials

The database automatically seeds standard roles upon initial boot:

| Role | Authorized Node Email | Cryptographic Key | Portal Route |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@hospital.com` | `password` | `/admin/dashboard` |
| **Medical Officer (Doctor)** | `sarah@hospital.com` | `password` | `/doctor/dashboard` |
| **Citizen (Patient)** | `patient@hospital.com` | `password` | `/patient/dashboard` |

---

## 📁 Repository Structure

```text
CareSoft-Health-Bridge/
├── hospital-management-springboot/       # Core Spring Boot Application
│   ├── src/main/java/com/hms/hospital/   # Controller, Model, Service, Repo, Security
│   ├── src/main/resources/
│   │   ├── static/js/                    # caresoft-security-guard.js
│   │   ├── templates/                    # Cyber-clinical HTML views (Thymeleaf/Decoupled)
│   │   │   ├── admin/                    # Command console, user directory, appointment logs
│   │   │   ├── appointment/              # Booking protocol & telemetry matrix
│   │   │   ├── doctor/                   # Daily queue & TimeGrid weekly agenda
│   │   │   └── patient/                  # Citizen health dashboard
│   │   └── application.properties        # Server & DB configurations
│   ├── pom.xml                           # Maven dependencies
│   └── test_qa.ps1                       # Automated QA verification script
├── workflows/                            # Architecture & Technical Documentation
│   ├── backend_workflow.md               # Controller, security, and service flows
│   ├── database_workflow.md              # Schema, entity relations, and JPA persistence
│   ├── frontend_workflow.md              # UI/UX design tokens and event binding
│   └── end_to_end_system_workflow.md     # Full integration lifecycle
└── README.md                             # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
- **Java SE Development Kit 17+**
- **Git**

### Installation & Execution

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SunBreaThing441/CareSoft-Health-Bridge.git
   cd CareSoft-Health-Bridge/hospital-management-springboot
   ```

2. **Launch the Spring Boot Application:**
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows:**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```

3. **Access the Application:**
   - Open your browser at: [http://localhost:8080/login](http://localhost:8080/login)
   - Use the pre-configured credentials or register a new telemetry profile.

---

## 🔒 Security Architecture Highlights

1. **Decoupled CSRF Protection**: All state-modifying requests (`POST`, `PUT`, `DELETE`) utilize the `X-CSRF-TOKEN` HTTP header extracted at runtime, replacing legacy hidden form inputs.
2. **Client-Side Route Guarding**: `caresoft-security-guard.js` inspects DOM session metadata and prevents unauthorized role traversal across portal zones.
3. **Smart Role Router**: Centralized routing via `/dashboard` redirects authenticated users to their authorized portal without redirection loops.

---

## 📄 License & Governance

Developed under the operational guidelines for the **CareSoft Health Bridge: UNESCO Global Health Logistics** initiative.
