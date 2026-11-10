# 📚 CareSoft Health Bridge: UNESCO Global Health Logistics - Architecture & Workflows

Welcome to the comprehensive technical documentation and workflow specifications for **CareSoft Health Bridge: UNESCO Global Health Logistics**.

---

## 📑 Workflow Documentation Index

| Document | Focus Area | Contents |
| :--- | :--- | :--- |
| 📖 [**Frontend Tier Workflow**](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/frontend_workflow.md) | Client & UI Layer | Cyber-clinical design system, Tailwind CSS tokens, Server-Side Rendering (SSR) via Thymeleaf, FullCalendar.js event lifecycles, and role-based command portals. |
| ⚙️ [**Backend Tier Workflow**](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/backend_workflow.md) | Business & Logic Layer | Spring Boot layered architecture, Spring Security 6 authentication/authorization filter chain, `/dashboard` role-based dispatching, Controller/Service/Repository layer contracts, and OpenAPI 3.0. |
| 🗄️ [**Database Tier Workflow**](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/database_workflow.md) | Persistence Layer | Hibernate 7 JPA mappings, Entity-Relationship (ER) diagram, schema definitions (`users`, `patients`, `appointments`), foreign key cascade behaviors, H2 file-based persistence, and query optimization. |
| 🔄 [**End-to-End System Workflow**](file:///D:/PROJECT_S/Spring%20Boot%20HMS/workflows/end_to_end_system_workflow.md) | Integration & Topology | Holistic system topology flowchart, complete cross-tier sequence diagrams (Citizen Registration, Doctor Agenda, Booking flow, Admin Governance), and endpoint verification matrix. |

---

## 🚀 Quick Reference: Pre-Configured Accounts

| Role | Email | Password | Access Portal |
| :--- | :--- | :--- | :--- |
| **Administrator (Global Director)** | `admin@hospital.com` | `password` | `/admin/dashboard` |
| **Doctor (Field Medical Officer)** | `sarah@hospital.com` | `password` | `/doctor/dashboard` |
| **Patient (Citizen)** | `patient@hospital.com` | `password` | `/patient/dashboard` |

---

## 💻 Tech Stack Summary
- **Backend**: Spring Boot 4.0.0 / 3.x, Java 21 LTS, Spring Security 6, Spring Data JPA.
- **Frontend**: Thymeleaf Templates, Tailwind CSS (Cyber-Clinical Design System), FullCalendar.js v6.1.15, Font Awesome 6.5.0.
- **Database**: H2 (file-based persistent mode with PostgreSQL compatibility) / PostgreSQL 16.
