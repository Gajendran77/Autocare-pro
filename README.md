# AutoCare Pro — Vehicle Service Management System

A premium, full-stack vehicle service management platform built with **Spring Boot 3**, **Spring Security**, **Thymeleaf**, **Bootstrap 5**, and **MySQL**. Styled with a dark-blue glassmorphism theme inspired by Tesla, BMW, Mercedes-Benz, Porsche, and Apple.

## Features

- Role-based auth (Admin / Customer / Mechanic) with Spring Security, BCrypt, remember-me, forgot/reset password
- Premium landing page with hero section, Car/Bike/Truck service categories
- Customer dashboard: vehicle management, service booking, live tracking, history, PDF invoices, QR service records
- Admin dashboard: stats, Chart.js analytics, booking/mechanic assignment, customer & mechanic management, inventory, payments
- Mechanic dashboard: assigned jobs, repair checklist, progress updates, before/after photo uploads
- Dark/light mode, toast notifications, animated counters, skeleton loading, ripple buttons

## Tech Stack

Java 17 · Spring Boot 3.2.5 · Spring Security · Spring Data JPA · Thymeleaf · MySQL 8 · Maven · Bootstrap 5 · Chart.js · iText7 (PDF) · ZXing (QR codes)

## Getting Started

### 1. Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8+ running locally

### 2. Configure the database
Edit `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

The app will auto-create the `autocare_pro` schema and tables on first run (`ddl-auto=update`).

To load demo data, run the SQL files manually after the first startup:

```bash
mysql -u root -p autocare_pro < src/main/resources/db/schema.sql
mysql -u root -p autocare_pro < src/main/resources/db/data.sql
```

> Note: `data.sql` ships with password hashes for the placeholder password `Password@123`.
> Because bcrypt hashes are salt-randomized, **regenerate real hashes** before using in
> production — the included hash is illustrative. Easiest path: register new accounts
> through the `/register` page instead of relying on seed data, then manually promote
> a user to `ADMIN` in the `users` table.

### 3. Build & run

```bash
mvn clean install
mvn spring-boot:run
```

Visit **http://localhost:8080**

### 4. Demo login
Register a Customer or Mechanic account via `/register`. To create an Admin, register normally
then update their role directly in MySQL:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```

## Project Structure

```
src/main/java/com/autocarepro/
├── config/          Security & Web MVC configuration
├── controller/       MVC controllers (Home, Auth, Customer, Admin, Mechanic) + REST API
├── dto/              Request/response DTOs
├── entity/           JPA entities
├── exception/        Global exception handling
├── repository/       Spring Data JPA repositories
├── security/         UserDetails, auth success handler
└── service/          Business logic (+ impl package)

src/main/resources/
├── db/               schema.sql, data.sql
├── static/css/       theme.css (design system)
├── static/js/        main.js, dashboard.js
└── templates/        Thymeleaf views (landing, auth, customer, admin, mechanic, fragments)
```

## Notes on this build

- Vehicle/category images are hotlinked from Unsplash (royalty-free) — swap for your own CDN in production.
- Email confirmation is wired up via `spring-boot-starter-mail` but disabled by default (`app.mail.enabled=false`) — configure SMTP credentials and an `EmailService` to enable.
- This was generated as a complete reference implementation; review and harden security settings (session config, CSRF on any AJAX POSTs, file upload validation) before deploying to production.
