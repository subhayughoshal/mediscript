# MediScript Project Development State

Last Updated: 2026-08-19
Project: MediScript
Purpose: Digital Prescription Web Application

---

# 1. DEVELOPMENT ENVIRONMENT

OS:
- Windows

Java:
- Java 21.0.12 LTS
- JAVA_HOME configured

Build:
- Maven Wrapper
- `mvnw.cmd`

IDE:
- IntelliJ IDEA

Spring Boot:
- 4.1.0

Database:
- PostgreSQL
- Database name: mediscript_db
- PostgreSQL version observed: 18.6

Git:
- Repository initialized
- Branch: main
- Initial commit:
  `5c26b22 Initial MediScript backend setup`

---

# 2. CURRENT APPLICATION STATUS

Application currently starts successfully.

Expected startup message:

`Started MediscriptBackendApplication`

Server:
- Port 8080

PostgreSQL connection:
- Working

Hibernate:
- Working
- PostgreSQLDialect detected correctly

---

# 3. ENVIRONMENT VARIABLES

Required:

DB_PASSWORD
JWT_SECRET

Current PowerShell session:
- DB_PASSWORD is set
- JWT_SECRET was initially missing
- JWT_SECRET was subsequently set to a sufficiently long random secret

IMPORTANT:
- Never commit actual secret values to Git.
- Never put real secrets in source code.

---

# 4. DATABASE

Database:

`mediscript_db`

Existing tables:

## roles

Columns:

- id
- name

Current roles:

1. ADMIN
2. DOCTOR
3. PATIENT
4. RECEPTIONIST

## users

Columns:

- id
- name
- email
- password
- phone
- role_id
- status
- created_at
- updated_at

Relationship:

users.role_id -> roles.id

Email is unique.

---

# 5. PROJECT PACKAGE STRUCTURE

Current feature-based structure:

com.mediscript.backend

├── auth
│   ├── controller
│   │   └── AuthController.java
│   ├── dto
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   └── service
│       ├── AuthenticationService.java
│       └── JwtService.java
│
├── config
│   ├── RoleDataInitializer.java
│   └── SecurityConfig.java
│
├── user
│   ├── controller
│   │   └── UserController.java
│   ├── dto
│   │   ├── CreateUserRequest.java
│   │   └── UserResponse.java
│   ├── entity
│   │   ├── Role.java
│   │   └── User.java
│   ├── repository
│   │   ├── RoleRepository.java
│   │   └── UserRepository.java
│   └── service
│       └── UserService.java
│
└── MediscriptBackendApplication.java

---

# 6. COMPLETED USER MODULE

## Role Entity
DONE

## User Entity
DONE

User fields:

- id
- name
- email
- password
- phone
- role
- status
- createdAt
- updatedAt

Role relationship:

@ManyToOne

users.role_id references roles.id

## RoleRepository
DONE

Contains:

`findByName(String name)`

## UserRepository
DONE

Contains:

`findByEmail(String email)`

`existsByEmail(String email)`

## UserService
DONE

Handles:

- duplicate email checking
- role lookup
- user creation
- password encoding

IMPORTANT:
BCrypt password hashing has ALREADY BEEN IMPLEMENTED.

DO NOT recreate BCrypt password hashing.

## CreateUserRequest
DONE

Fields:

- name
- email
- password
- phone
- role

## UserResponse
DONE

Created so that User entities/passwords are not directly exposed in API responses.

## UserController
DONE

Endpoint:

POST /api/users

---

# 7. SECURITY

Spring Security dependency:
DONE

SecurityConfig:
DONE

PasswordEncoder:
DONE

BCrypt:
DONE

IMPORTANT:
Password hashing has already been implemented in UserService.

Do NOT tell the developer to add BCrypt again unless inspecting the current code proves it is missing or broken.

---

# 8. JWT AUTHENTICATION

JWT dependency/configuration:
IN PROGRESS

JwtService:
EXISTS

AuthenticationService:
EXISTS

AuthController:
EXISTS

LoginRequest:
EXISTS

LoginResponse:
EXISTS

JWT secret:
CONFIGURED through environment variable

JWT expiration:
86400000 milliseconds

IMPORTANT:
JWT secret must be >= 256 bits for HS256.

A previous startup error occurred because the secret was only 224 bits.

That issue has been fixed by setting a sufficiently long random secret.

## Current Authentication Progress

### BCrypt Password Hashing
- BCrypt password hashing is implemented in `UserService`.
- New users are saved with BCrypt-hashed passwords.
- Password verification is performed using `PasswordEncoder.matches()`.
- Temporary test users were created to verify BCrypt authentication.

### JWT Authentication
- `JwtService` has been implemented.
- JWT contains:
    - email as the subject
    - userId claim
    - role claim
    - issued-at timestamp
    - expiration timestamp
- JWT is signed using the configured secret key.
- `AuthenticationService`:
    - finds the user by email
    - verifies the password using BCrypt
    - checks that the user status is `ACTIVE`
- `LoginResponse` returns:
    - JWT token
    - user ID
    - name
    - email
    - role
- `AuthController` exposes:
    - `POST /api/auth/login`

### JWT Login Verification
JWT login has been successfully tested with:

- Email: `jwt.doctor@test.com`
- Role: `DOCTOR`
- User ID: `4`
- Password authentication: SUCCESS
- JWT generation: SUCCESS
- Login response: SUCCESS

The password used for this test user must NOT be stored in this project state file.

### Next Step
Implement JWT request authentication using a JWT authentication filter so that requests containing:

`Authorization: Bearer <JWT>`

are validated and the authenticated user is placed into Spring Security's `SecurityContext`.

After that, test protected endpoints and then implement role-based authorization.
---

# 9. APPLICATION.YAML

Current structure:

spring:
application:
name: mediscript-backend

datasource:
url: jdbc:postgresql://localhost:5432/mediscript_db
username: postgres
password: ${DB_PASSWORD}

jpa:
hibernate:
ddl-auto: update
show-sql: true
properties:
hibernate:
format_sql: true

jwt:
secret: ${JWT_SECRET}
expiration: 86400000

server:
port: 8080

IMPORTANT:
`jwt` is a TOP-LEVEL property.

It must NOT be nested inside `spring`.

---

# 10. VERIFIED FUNCTIONALITY

The following has been tested successfully:

1. Spring Boot application starts.
2. PostgreSQL connection works.
3. Hibernate initializes.
4. roles table exists.
5. users table exists.
6. Role data initializer creates:
    - ADMIN
    - DOCTOR
    - PATIENT
    - RECEPTIONIST
7. POST /api/users successfully created a test doctor.
8. users.role_id correctly references roles.id.
9. Spring Security dependency resolves.
10. BCrypt password hashing has been implemented.
11. JWT service exists.
12. JWT secret issue has been resolved.
13. Application currently starts successfully.

---

# 11. TEMPORARY TEST USER

A test user was previously created:

Name:
Dr. Test

Email:
doctor@test.com

Role:
DOCTOR

This user was originally created before BCrypt security was completed.

IMPORTANT:
If this user still exists, do not assume its stored password is BCrypt-hashed.

Verify before using it for authentication.

---

# 12. GIT

Current branch:

main

Initial commit:

5c26b22
Initial MediScript backend setup

Current changes after that commit include the authentication/JWT/security work.

IMPORTANT:
Before committing:
1. git status
2. inspect changes
3. git add .
4. git commit with a meaningful message

Never commit:
- passwords
- DB secrets
- JWT secrets
- API keys
- generated secret values

---

# 13. RECENT ERRORS THAT ARE ALREADY FIXED

## Error 1

Hibernate:

`Unable to determine Dialect without JDBC metadata`

Investigation showed PostgreSQL itself was working.

Current logs confirm:

`Database dialect: PostgreSQLDialect`

Therefore this is NOT currently an active problem.

Do not change Hibernate dialect configuration unnecessarily.

---

## Error 2

Missing:

`jwt.expiration`

Cause:

jwt was incorrectly nested under spring in application.yaml.

Fixed by making:

jwt:

a top-level YAML property.

---

## Error 3

JWT:

`WeakKeyException`

Cause:

JWT secret was only 224 bits.

Fixed by generating a sufficiently long random secret.

---

## Error 4

JWT_SECRET environment variable was missing.

Fixed by setting JWT_SECRET in the PowerShell environment.

---

# 14. CURRENT EXACT POSITION

The application now starts successfully.

The next development task is NOT:

- creating User
- creating Role
- creating UserRepository
- creating UserService
- adding BCrypt
- creating SecurityConfig
- creating JwtService

Those are already done.

NEXT TASK:

Inspect and verify the existing authentication/JWT implementation.

Specifically inspect:

1. JwtService.java
2. AuthenticationService.java
3. AuthController.java
4. LoginRequest.java
5. LoginResponse.java
6. SecurityConfig.java
7. UserResponse.java
8. UserService.java

Then test:

POST /api/auth/login

using a user whose password has been BCrypt-hashed.

Only after authentication is verified should development proceed to JWT authorization/security filters.

---

# 15. DEVELOPMENT RULES

IMPORTANT RULES FOR FUTURE DEVELOPMENT:

1. Do not recreate completed classes.
2. Do not ask the developer to add BCrypt again.
3. Do not change working configuration without identifying the reason.
4. Before changing code, inspect the current implementation.
5. When an error occurs, identify the root cause from the actual stack trace.
6. Make one logical change at a time.
7. Test after each significant change.
8. Do not introduce unnecessary dependencies.
9. Do not expose passwords in API responses.
10. Never store plain-text passwords.
11. Never commit secrets.
12. Keep the database relational and normalized.
13. Maintain the existing feature-based package structure.
14. Preserve Java 21.
15. Preserve PostgreSQL.
16. Preserve Spring Boot 4.1.0 unless there is a concrete reason to change it.
17. Do not jump ahead to later modules before the current authentication layer is verified.
18. Before giving instructions, compare them against this project state.

---

# 16. SESSION CHECKPOINT FORMAT

At the end of every development session, update this file with:

DATE:
CURRENT STATUS:
WHAT WAS COMPLETED:
FILES CREATED:
FILES MODIFIED:
DATABASE CHANGES:
TESTS PERFORMED:
ERRORS ENCOUNTERED:
ERRORS FIXED:
CURRENT NEXT TASK:
GIT COMMIT:
IMPORTANT NOTES:
