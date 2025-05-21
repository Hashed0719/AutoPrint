# AutoPrint Backend

This is the backend service for the AutoPrint application, built with Spring Boot 3.x and Spring Security.

## Features

- User registration with email and password
- JWT-based authentication
- Role-based access control
- H2 in-memory database for development
- PostgreSQL support for production
- API documentation (coming soon)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- (Optional) PostgreSQL for production

## Getting Started

### Running the Application

1. Clone the repository
2. Build the project:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```

### Testing the Registration Endpoint

You can test the registration endpoint using curl or Postman:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### Default Admin User

A default admin user is created on application startup:
- Username: `admin`
- Password: `admin123`

### Database

- **Development**: H2 in-memory database (http://localhost:8080/h2-console)
  - JDBC URL: jdbc:h2:mem:autoprintdb
  - Username: sa
  - Password: (leave empty)

- **Production**: Update `application.properties` with your PostgreSQL credentials

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate and get JWT token (coming soon)
- `POST /api/auth/refresh-token` - Refresh JWT token (coming soon)

## Security

- JWT-based authentication
- Password hashing with BCrypt
- CSRF protection
- CORS configuration

## License

This project is licensed under the MIT License.
