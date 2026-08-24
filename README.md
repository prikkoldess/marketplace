# Mini Marketplace Backend

## About the Project
This project is a core backend application for a Mini Marketplace. It serves as the engine for an e-commerce platform where buyers can register, browse products from various sellers, manage their shopping baskets, and seamlessly place orders. For sellers (merchants), it provides the ability to manage their inventory and track incoming purchases. The system is designed to handle the entire lifecycle of an online marketplace, ensuring secure transactions, independent order processing for different merchants, robust administration, and reliable data management.

## Technologies Used
* **Core Framework:** Java 21, Spring Boot 3.5.x
* **Security:** Spring Security, JWT 
* **Data Access & Database:** Spring Data JPA, Hibernate, PostgreSQL 16
* **Database Migrations:** Flyway
* **Caching & Token Management:** Redis (used for Refresh Tokens)
* **Message Broker (Asynchronous Processing):** RabbitMQ
* **Email Testing:** Mailhog
* **Containerization & DevOps:** Docker, Docker Compose
* **CI/CD:** GitHub Actions
* **API Documentation:** Swagger / OpenAPI 3
* **Testing:** JUnit 5, Mockito (Unit Tests)

## Key Features
* **User & Authentication Management:** Secure user registration, login, and role-based access control (**BUYER, SELLER, ADMIN**). Implements robust session management using JWT and refresh tokens stored in Redis.
* **Admin Capabilities:** Dedicated administration endpoints for moderating the platform. Admins can globally view baskets, manage orders, moderate products (hide/unlock/delete), and block/activate user accounts.
* **Multi-Merchant Order Processing:** 
    Basket processing logic: when a customer proceeds to checkout, the system automatically analyzes the items, groups them (using the `OrderGroup` entity), and splits them into independent sub-orders for each specific seller. This allows sellers to manage only their own sales, while enabling the customer to pay for everything in a single transaction.
* **Product Management:** Full product catalog capabilities, including accurate inventory `quantity` tracking. Product endpoints support 
* **Advanced Order Processing:** Complete lifecycle tracking for orders, including statuses like `CANCELLED` and `DELIVERED`.
* **RabbitMQ Price Alerts & Notifications:**
    Using a message broker to decouple business logic and send asynchronous notifications:
* **Price Alerts (Price Drops):** Automatic generation and dispatch of email notifications to users when a seller lowers the price of an item on their wishlist. Messages are routed via delayed queues (Dead Letter Exchange) to optimize load.
* **Cancellation Notifications:** Instant asynchronous notification of sellers when a buyer cancels an order, accompanied by the automatic return of stock to warehouse inventory.
* **Global Exception Handling:** Centralized error handling using `@RestControllerAdvice` to ensure the API always returns standardized, predictable error responses.

## How to Run the Application

1.  **Environment Configuration:**
    Ensure that the `.env` file is present in the root directory alongside `docker-compose.yml`. This file contains the necessary environment variables for the database, Redis, and RabbitMQ. Example configuration:
    ```env
    POSTGRES_DB=marketplace_db
    POSTGRES_USER=market_admin
    POSTGRES_PASSWORD=marketpass
    SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/marketplace_db
    SPRING_DATASOURCE_USERNAME=market_admin
    SPRING_DATASOURCE_PASSWORD=marketpass
    JWT_SECRET_KEY=your_secure_jwt_secret_key_here
    ADMIN_PASSWORD=adminpass
    ADMIN_FIRST_NAME=Head
    ADMIN_LAST_NAME=Admin
    DMIN_EMAIL=admin@gmail.com
    ```

2. **Run with Docker Compose:**
    Clone the repository.
    Run the command in the project's root folder:
```bash
docker-compose up -d --build
```