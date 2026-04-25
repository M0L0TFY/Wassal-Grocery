# Wassal Grocery 🛒

A REST API backend application connecting multiple grocery stores with clients.

## 📚 Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.4
- **Database/Cache:** PostgreSQL 18, MongoDB 8.2, Redis 8.2
- **Infrastructure:** Docker & Docker Compose

## ✨ Main Features
- **User Authentication:** Secure Login/Register using Spring Security, and Stateless JWT.
- **Store Management:** CRUD operations for stores protected by Role-Based Access Control.
- **Shopping Cart:** Fast shopping cart system using Redis.
- **Order System:** Converts cart items into permanent order records.

## 🚀 How to Run

### Prerequisites
- [Git](https://git-scm.com/install/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### 1. Clone the repository
```bash
git clone https://github.com/M0L0TFY/Wassal-Grocery.git
cd Wassal-Grocery/wassal-backend
```
### 2. Setup Environment Variables
Create a ``.env`` file in the root folder based on ``.env.template``.
```bash
# On Linux/macOS/Git Bash:
cp .env.template .env

# On Windows:
copy .env.template .env
```

### 3. Build and Run
Run the following command in the terminal.
```bash
docker compose up --build -d
```
Once the application is running, you can explore the API endpoints via Swagger UI:

👉 http://localhost:8080/swagger-ui/index.html

## 📄 License
This project is licensed under the [MIT LICENSE](https://github.com/M0L0TFY/Wassal-Grocery/blob/main/LICENSE).