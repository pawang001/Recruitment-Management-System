# Recruitment Management System Backend

This project is a complete backend server for a Recruitment Management System, built with Spring Boot as part of a technical assignment. It provides a RESTful API for managing users (Admins & Applicants), job openings, and job applications, including resume parsing via a third-party API.


---

## ## Features

-   **User Management**: Secure signup and login for Admins and Applicants.
-   **Authentication**: JWT-based authentication to secure endpoints.
-   **Authorization**: Role-based access control (Admin vs. Applicant).
-   **Job Management**: Admins can create and view job openings and their applicants.
-   **Resume Processing**: Applicants can upload resumes (PDF/DOCX), which are automatically parsed to extract key information.
-   **Application Logic**: Enforces that applicants must upload a resume before they can apply for a job.
-   **Database**: Uses MySQL for persistent data storage.
-   **API Documentation**: Interactive API documentation via Swagger UI.

---

## ## Project Structure

The project follows a standard layered architecture to ensure separation of concerns and maintainability:

-   `config`: Contains all configuration classes, including `SecurityConfig` and `ApplicationConfig`.
-   `controller`: Handles all incoming HTTP requests and API endpoints.
--   `dto`: (Data Transfer Objects) Classes for transferring data between the client and server.
-   `exception`: A global exception handler for consistent error responses.
-   `model`: JPA entities representing the database tables.
-   `repository`: Spring Data JPA interfaces for database operations.
-   `security`: JWT utility classes and `UserDetailsService` implementation.
-   `service`: Contains the core business logic of the application.

---

## ## Setup and Run Instructions

### Prerequisites

-   JDK 17 or higher
-   Maven 3.6+
-   MySQL Server

### 1. Database Setup

1.  Ensure your MySQL server is running.
2.  Connect to your MySQL instance and create the database:
    ```sql
    CREATE DATABASE recruitmentdb;
    ```

### 2. Configuration

1.  Open the `src/main/resources/application.properties` file.
2.  Update the MySQL credentials to match your local setup:
    ```properties
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    ```

> **Note on Secrets**: For this assignment, API keys and secrets have been left in the `application.properties` file for ease of testing. In a production environment, these would be managed securely using environment variables or a secrets management service like HashiCorp Vault.

### 3. Build and Run the Application

1.  Clone the repository.
2.  Navigate to the project root and run a clean build:
    ```bash
    mvn clean install
    ```
3.  Run the application using Maven:
    ```bash
    mvn spring-boot:run
    ```
4.  The application will start on `http://localhost:8081`.

---

## ## API Documentation & Testing

Once the application is running, the interactive Swagger UI documentation is available at:

**`http://localhost:8081/swagger-ui.html`**

You can use the Swagger UI to test all the API endpoints.

### Main View
The main view lists all available controllers and their endpoints.

![Swagger Main View](docs/images/swagger-main-1.png)
![Swagger Main View](docs/images/swagger-main-2.png)

### Authentication Flow
To access secured endpoints, you must first authenticate.

1.  Use the `POST /api/auth/login` endpoint with a valid user's credentials to get a JWT.
2.  Click the **Authorize** button at the top of the page.
3.  In the pop-up, paste the token in the format `Bearer <your_token>`.

![Swagger Login Endpoint](docs/images/swagger-login-endpoint.png)
![Swagger Authorize Modal](docs/images/swagger-authorize-modal.png)

---

## ## API Endpoints Overview

| Method | Path                               | Description                                      | Role Required |
| :----- | :--------------------------------- | :----------------------------------------------- | :------------ |
| `POST` | `/api/auth/signup`                 | Create a new user account                        | Public        |
| `POST` | `/api/auth/login`                  | Authenticate and receive a JWT                   | Public        |
| `POST` | `/api/admin/job`                   | Create a new job opening                         | **ADMIN** |
| `GET`  | `/api/admin/job/{id}`              | Get details of a specific job opening            | **ADMIN** |
| `GET`  | `/api/admin/applicants`            | Get a list of all applicants                     | **ADMIN** |
| `GET`  | `/api/admin/applicant/{id}`        | Get a specific applicant's profile data          | **ADMIN** |
| `POST` | `/api/applicant/uploadResume`      | Upload and process a resume                      | **APPLICANT** |
| `GET`  | `/api/jobs`                        | Fetch all available job openings                 | Authenticated |
| `POST` | `/api/jobs/apply?job_id={id}`      | Apply for a specific job                         | **APPLICANT** |
