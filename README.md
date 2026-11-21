<div align="center">

# 🥤 Whey Store

### An e-commerce platform for sports nutrition

Discover products, place orders, and track purchases through the online store. Administrators and staff manage products, inventory, orders, and reports through dedicated portals.

<p>
  <img alt="Angular" src="https://img.shields.io/badge/Angular-22-DD0031?logo=angular&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=springboot&logoColor=white">
  <img alt="Java" src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white">
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white">
  <img alt="Docker Compose" src="https://img.shields.io/badge/Docker%20Compose-ready-2496ED?logo=docker&logoColor=white">
</p>

</div>

---

## ✨ Overview

Whey Store is an online shop for whey and sports nutrition products. It provides three separate Angular applications for customers, administrators, and staff. The backend uses Spring Boot services organized by business domain and exposes them through an API Gateway.

## 🚀 Features

### 🛍️ Customer store

- Browse, search, filter, and sort products.
- View product details.
- Add products to a cart, with guest cart support and cart merging after sign-in.
- Register and sign in with authentication and role management through Keycloak.
- Place orders and view order history and order details.

### 🧰 Admin and staff portals

- Manage products, orders, inventory, and user accounts.
- Review customers, stock levels, and business activity through dashboards.
- Generate revenue, inventory, invoice, and top-selling product reports.
- Process order workflows and send event-based email notifications.

> Payment is currently implemented as a development mock. Configure and integrate a real payment provider before using the application in production.

## 🧱 Architecture

```text
┌───────────────────────────────────────────────────┐
│ Angular Store · Admin · Staff                     │
└──────────────────────┬────────────────────────────┘
                       │ REST API
                 ┌─────▼─────┐
                 │ API Gateway│ ─── Keycloak
                 └─────┬─────┘
                       │
    ┌──────────┬────────┼─────────┬───────────┐
    ▼          ▼        ▼         ▼           ▼
 Products   Inventory  Cart     Orders     Payments
                                  │            │
                             Workflow · Notifications · Reports
    └──────────┬────────┴─────────┴───────────┘
               ▼
       PostgreSQL · Redis · Kafka
```

**Backend services:** API Gateway, Product, Inventory, Cart, Order, Payment, Workflow, Notification, and Report. PostgreSQL stores business data; Redis supports cart storage and request rate limiting; Kafka carries events between services. Keycloak handles authentication and authorization.

## 🛠️ Technology stack

| Area | Technology |
| --- | --- |
| Frontend | Angular 22, TypeScript, SCSS, Three.js |
| Backend | Java 17, Spring Boot 3.2, Spring Cloud Gateway |
| Business workflows | Camunda 7, Apache Kafka |
| Storage and cache | PostgreSQL 16, Redis 7 |
| Authentication | Keycloak |
| Reporting | JasperReports |
| Runtime and build | Docker Compose, Maven, npm |

## ⚙️ Getting started

### Requirements

- Node.js and npm compatible with Angular CLI 22.
- Java 17 and Maven (when running the backend outside Docker).
- Docker Engine and the Docker Compose plugin.

### 1. Configure the frontend

From the project root:

```bash
cd Frontend
cp .env.example .env
npm install
```

By default, the frontend connects to the API at `http://localhost:8080/api` and Keycloak at `http://localhost:8090`. Update `Frontend/.env` if your environment uses different addresses.

### 2. Start the backend

From the project root:

```bash
cd Backend
docker compose up --build
```

Compose starts the backend services along with PostgreSQL, Redis, Kafka, Keycloak, and MailHog. The first startup may take longer while Docker downloads images and builds the services.

### 3. Run the frontend applications

Open a new terminal:

```bash
cd Frontend
npm start
```

Run the portals individually:

```bash
npm run start:admin   # http://localhost:4201
npm run start:staff   # http://localhost:4202
```

The customer store runs at `http://localhost:4200`. To run all three applications at once:

```bash
npm run start:all
```

### Default service URLs

| Service | URL |
| --- | --- |
| Customer store | `http://localhost:4200` |
| Admin portal | `http://localhost:4201` |
| Staff portal | `http://localhost:4202` |
| API Gateway | `http://localhost:8080` |
| Keycloak | `http://localhost:8090` |
| MailHog UI | `http://localhost:8025` |

## 🧑‍💻 Development

### Build the frontend

```bash
cd Frontend
npm run build       # Customer store
npm run build:admin
npm run build:staff
npm run build:all
```

### Build the backend with Maven

```bash
cd Backend
mvn clean package
```

### Environment configuration

- `Frontend/.env.example`: API Gateway, Keycloak, frontend ports, and sample payment integration variables.
- `Backend/.env.example`: reference variables for backend services running outside the Docker Compose configuration.
- `Backend/docker-compose.yml`: infrastructure and backend services for the Docker environment.

Do not commit secrets or real credentials. Default values in the development configuration are intended for local use only.

## 📁 Project structure

```text
.
├── Backend/
│   ├── api-gateway/
│   ├── product-service/
│   ├── inventory-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── workflow-service/
│   ├── notification-service/
│   ├── report-service/
│   ├── db-migrations/
│   └── docker-compose.yml
├── Frontend/
│   └── src/app/
│       ├── customer/
│       ├── admin/
│       ├── staff/
│       └── shared/
└── keycloak/
    ├── imports/
    └── themes/
```

## 🤝 Contributing

1. Create a branch for your changes.
2. Keep each change focused on a specific goal.
3. Describe your changes clearly in the pull request.

## 📄 License

No license is currently declared in this repository. Add a `LICENSE` file before distributing the project under a specific license.
