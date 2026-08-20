# MediScript Project Development State

Last Updated: 2026-08-20
Project: MediScript
Purpose: Digital Prescription Web Application

---

# 1. PROJECT OVERVIEW

MediScript is a digital prescription web application.

Current development focus:
- Backend development
- Authentication and authorization
- User and role management
- PostgreSQL persistence

Frontend development has not started yet.

---

# 2. DEVELOPMENT ENVIRONMENT

OS:
- Windows

Java:
- Java 21.0.12 LTS
- JAVA_HOME configured

IDE:
- IntelliJ IDEA

Build:
- Maven Wrapper
- `mvnw.cmd`

Spring Boot:
- 4.1.0

Database:
- PostgreSQL
- Database name: `mediscript_db`
- PostgreSQL version observed: 18.6

Git:
- Repository initialized
- Branch: `main`
- Remote: `origin`
- Current Git commit:
  `133f2c4 Add admin user management and role authorization`

---

# 3. APPLICATION STATUS

Application starts successfully.

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

# 4. ENVIRONMENT VARIABLES

Required:

- `DB_PASSWORD`
- `JWT_SECRET`

Current configuration:

- `DB_PASSWORD` is configured
- `JWT_SECRET` is configured
- JWT secret is sufficiently long for the configured signing algorithm

IMPORTANT:
- Never commit actual secret values.
- Never put real secrets in source code.
- Never put passwords, JWT secrets, API keys, or other credentials in this file.

---

# 5. DATABASE

Database:

`mediscript_db`

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

`users.role_id -> roles.id`

Email is unique.

---

# 6. CURRENT PACKAGE STRUCTURE

Current feature-based structure:

com.mediscript.backend

├── auth
│   ├── controller
│   │   └── AuthController.java
│   ├── dto
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   ├── filter
│   │   └── JwtAuthenticationFilter.java
│   └── service
│       ├── AuthenticationService.java
│       └── JwtService.java
│
├── admin
│   └── controller
│       └── AdminUserController.java
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

# 7. USER AND ROLE MODULE

## Role Entity

Status:
- DONE

Role fields:

- id
- name

---

## User Entity

Status:
- DONE

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

Relationship:

- User -> Role
- `@ManyToOne`
- `users.role_id -> roles.id`

---

## RoleRepository

Status:
- DONE

Contains:

`findByName(String name)`

---

## UserRepository

Status:
- DONE

Contains:

`findByEmail(String email)`

`existsByEmail(String email)`

---

## UserService

Status:
- DONE

Handles:

- duplicate email checking
- role lookup
- user creation
- BCrypt password encoding
- saving users

IMPORTANT:

BCrypt password hashing is ALREADY IMPLEMENTED.

New passwords are encoded using:

`passwordEncoder.encode(password)`

Do NOT recreate BCrypt hashing.

---

## CreateUserRequest

Status:
- DONE

Fields:

- name
- email
- password
- phone
- role

---

## UserResponse

Status:
- DONE

Used to prevent password information from being returned in API responses.

---

# 8. PUBLIC USER REGISTRATION

Endpoint:

`POST /api/users`

Status:
- DONE
- VERIFIED

Public registration is intentionally restricted to the PATIENT role.

The role supplied by the public client is NOT trusted.

Current behavior:

```text
POST /api/users
        ↓
UserController
        ↓
UserService
        ↓
PATIENT role


A security test was performed where the client sent:

{
  "role": "ADMIN"
}

The resulting user was created as:

PATIENT

This confirms that anonymous users cannot create themselves as ADMIN through the public registration endpoint.

Password hashing was also verified for this flow.

9. ADMIN USER MANAGEMENT

Endpoint:

POST /api/admin/users

Status:

DONE
VERIFIED

Controller:

AdminUserController.java

Access:

ADMIN only

Admin users can create users with roles such as:

DOCTOR
RECEPTIONIST
PATIENT

Security configuration:

/api/admin/** -> hasRole("ADMIN")

The admin-created user's password is processed through the existing BCrypt implementation in UserService.

10. SPRING SECURITY

Status:

DONE
VERIFIED

SecurityConfig:

DONE

PasswordEncoder:

DONE

BCrypt:

DONE

Current authorization rules:

/api/auth/**      -> public
/api/users/**     -> public
/api/admin/**     -> ADMIN only
everything else   -> authenticated

Session policy:

STATELESS

CSRF:

Disabled for the backend API
11. JWT AUTHENTICATION

Status:

DONE
VERIFIED
JwtService

Status:

DONE

Responsibilities:

Generate JWT
Validate JWT
Extract email from JWT

JWT contains:

email as subject
userId claim
role claim
issuedAt
expiration

JWT is signed using the configured secret key.

JWT expiration:

86400000 milliseconds

AuthenticationService

Status:

DONE

Responsibilities:

Find user by email
Verify password using BCrypt
Verify account status is ACTIVE
Return authenticated User

Invalid credentials produce an authentication failure.

AuthController

Status:

DONE

Endpoint:

POST /api/auth/login

Successful login returns:

token
userId
name
email
role
LoginRequest

Status:

DONE

Fields:

email
password
LoginResponse

Status:

DONE

Fields:

token
userId
name
email
role
12. JWT AUTHENTICATION FILTER

File:

JwtAuthenticationFilter.java

Status:

DONE
VERIFIED

Responsibilities:

Read the Authorization header
Check for Bearer <JWT>
Validate the JWT
Extract the email
Load the current User from PostgreSQL
Check that the user is ACTIVE
Read the user's current database role
Create the appropriate Spring Security authority
Set the authenticated user in SecurityContext

Authorities are created using:

ROLE_<DATABASE_ROLE>

Examples:

ADMIN -> ROLE_ADMIN
DOCTOR -> ROLE_DOCTOR
PATIENT -> ROLE_PATIENT
RECEPTIONIST -> ROLE_RECEPTIONIST

IMPORTANT:

The database user and role are used as the authoritative source for current authorization.

13. AUTHENTICATION AND AUTHORIZATION TESTS

The following tests have been successfully completed.

JWT Login

Test user:

Email: jwt.doctor@test.com
Role: DOCTOR
User ID: 4

Result:

Password authentication: SUCCESS
JWT generation: SUCCESS
Login response: SUCCESS

The test password is intentionally NOT stored in this file.

Protected Endpoint

A temporary protected endpoint was created for testing.

Tests:

Request without JWT -> rejected
Request with valid JWT -> authenticated

Successful result:

Authenticated as: jwt.doctor@test.com

The temporary security test controller was removed after verification.

Doctor Authorization

A temporary doctor-protected endpoint was tested.

DOCTOR JWT:

Access -> SUCCESS

This verified:

DOCTOR
  ↓
ROLE_DOCTOR
  ↓
hasRole("DOCTOR")

The temporary test endpoint was removed after verification.

Admin Authorization

Endpoint:

POST /api/admin/users

Tests performed:

No JWT

Result:

403 Forbidden

DOCTOR JWT

Result:

403 Forbidden

ADMIN JWT

Result:

User creation succeeded.

The created DOCTOR user's password was verified as BCrypt-hashed in PostgreSQL.

Temporary ADMIN and test-created DOCTOR accounts were removed after testing.

Public Registration Security

A request attempting to register as ADMIN:

{
  "role": "ADMIN"
}

was tested.

Result:

PATIENT

This confirms that the public registration endpoint does not trust the client's requested role.

14. APPLICATION.YAML

Current important configuration:

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

jwt is a TOP-LEVEL property.

It must NOT be nested inside spring.

15. KNOWN FIXED ISSUES
Hibernate Dialect Error

Previous issue:

Unable to determine Dialect without JDBC metadata

Status:

FIXED

PostgreSQL connection and Hibernate dialect detection are currently working.

Do not change Hibernate dialect configuration unnecessarily.

Missing JWT Expiration

Previous issue:

jwt.expiration could not be resolved.

Cause:

jwt was incorrectly nested under spring.

Fix:

jwt is now a top-level YAML property.

Status:

FIXED
Weak JWT Secret

Previous issue:

WeakKeyException

Cause:

JWT secret was too short.

Fix:

A sufficiently long random secret was configured through JWT_SECRET.

Status:

FIXED
Missing JWT_SECRET

Previous issue:

JWT_SECRET environment variable was missing.

Fix:

JWT_SECRET was configured in the PowerShell environment.

Status:

FIXED
16. TEST USERS

Existing development/test data may include:

jwt.doctor@test.com

IMPORTANT:

Do not assume that older users have BCrypt passwords.

The original:

doctor@test.com

user was created before BCrypt was implemented and had a plain-text test password.

Do not use that account for BCrypt/JWT verification unless its password storage has first been checked.

Temporary test users created during security testing have been removed.

Never store test passwords in this file.

17. GIT CHECKPOINT

Current branch:

main

Current commit:

133f2c4 Add admin user management and role authorization

Remote:

origin/main

Latest verified state:

HEAD -> main
origin/main
working tree clean

IMPORTANT:

Before future commits:

git status
Inspect changes
git add .
git status
Commit with a meaningful message
git push origin main
Verify git status

Never commit:

passwords
DB secrets
JWT secrets
API keys
generated secret values
18. DEVELOPMENT RULES

These rules must be followed for future development:

Do not recreate completed classes.
Do not add BCrypt again.
Do not recreate JWT authentication.
Do not change working configuration without identifying the reason.
Before changing existing code, inspect the current implementation.
When an error occurs, identify the root cause from the actual stack trace/output.
Make one logical change at a time.
Build/test after significant changes.
Do not expose passwords in API responses.
Never store plain-text passwords.
Never commit secrets.
Keep the database relational and normalized.
Maintain the existing feature-based package structure.
Preserve Java 21.
Preserve PostgreSQL.
Preserve Spring Boot 4.1.0 unless there is a concrete reason to change it.
Do not jump ahead without completing and testing the current feature.
Compare proposed changes against this project state before giving implementation instructions.
Prefer modifying existing working code over creating duplicate implementations.
Keep temporary testing code separate and remove it after verification.
Update this state file after every major development checkpoint.
19. CURRENT DEVELOPMENT STATUS

Authentication and authorization foundation:

COMPLETE

User and role foundation:

COMPLETE

Public patient registration:

COMPLETE

Admin user management:

COMPLETE

JWT authentication:

COMPLETE

Role-based authorization:

COMPLETE

20. NEXT DEVELOPMENT PHASE

The authentication and authorization foundation is now complete.

The next development phase is the actual MediScript medical-domain functionality.

Recommended order:

Patient management
Doctor management
Doctor-patient relationship
Appointment management
Prescription management
Prescription medicines/details
Prescription viewing/sharing
Additional validation and exception handling
Frontend development
Integration testing

Before implementing a new module:

Inspect existing entities/repositories/services/controllers.
Define the database relationship.
Implement one logical layer at a time.
Compile/build.
Test the endpoint.
Update this state file.
Commit and push a clean checkpoint.
21. SESSION CHECKPOINT

DATE:
2026-08-20

CURRENT STATUS:
Authentication and authorization foundation complete.

WHAT WAS COMPLETED:

JWT authentication
JWT filter
SecurityContext authentication
Database-backed role authorization
Public registration restricted to PATIENT
Admin-only user creation
BCrypt verification
Security testing

FILES CREATED:

auth/filter/JwtAuthenticationFilter.java
admin/controller/AdminUserController.java

FILES MODIFIED:

auth/service/JwtService.java
config/SecurityConfig.java
user/controller/UserController.java
MEDISCRIPT_PROJECT_STATE.md

DATABASE CHANGES:

Temporary security test users created and removed.
Existing role structure preserved.

TESTS PERFORMED:

JWT login
JWT validation
Protected endpoint
DOCTOR authorization
ADMIN authorization
Public registration role restriction
BCrypt password storage

ERRORS ENCOUNTERED:

None during the final authorization checkpoint.

ERRORS FIXED:

Weak JWT secret
Missing JWT_SECRET
Incorrect JWT YAML nesting
Temporary plain-text password test-user issue

CURRENT NEXT TASK:
Begin the medical-domain modules, starting with patient management.

GIT COMMIT:
133f2c4 Add admin user management and role authorization

IMPORTANT NOTES:

Do not recreate authentication/security components.
Do not store secrets in this file.
Continue from this exact checkpoint.
