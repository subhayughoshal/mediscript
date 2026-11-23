# MediScript — Project Context & Handoff Documentation

> **Purpose:** This document provides the technical and historical context required to continue developing MediScript with another LLM or developer.
>
> **Last updated:** November 9, 2026
> **Project type:** Final-year B.Tech Computer Engineering project
> **Current development phase:** Backend development — patient module authorization and profile management
> **Primary objective:** Build a secure digital prescription and appointment management web application.

---

# 1. Project Overview

## 1.1 Project Name

**MediScript**

## 1.2 Project Description

MediScript is a digital prescription management web application designed to manage interactions between patients, doctors, receptionists, and administrators.

The backend is being developed using Spring Boot and PostgreSQL.

The system supports:

- User authentication and authorization.
- Role-based access control.
- Doctor profile management.
- Patient profile management.
- Doctor discovery.
- Appointment scheduling and management.
- Prescription creation and management.
- Prescription items.
- Administrative management and oversight.
- Audit logging.
- Dashboard statistics.

The application follows a role-based architecture with four primary roles:

1. `ADMIN`
2. `DOCTOR`
3. `PATIENT`
4. `RECEPTIONIST`

The project is being developed incrementally. Existing functionality should be preserved, and new features should be implemented without unnecessarily restructuring completed modules.

## 1.3 Important Development Approach

The project is being developed step by step.

The preferred workflow is:

1. Understand the existing implementation.
2. Inspect the relevant files before proposing changes.
3. Explain the planned modification.
4. Implement a small, logically complete feature.
5. Run Maven tests.
6. Verify the result.
7. Continue to the next feature.

Do not rewrite completed modules just to introduce a different coding style or architecture.

---

# 2. Tech Stack & Architecture

## 2.1 Backend Technology Stack

| Technology | Purpose | Status |
|---|---|---|
| Java 21 LTS | Primary programming language | In use |
| Spring Boot | Backend framework | In use |
| Spring Web | REST API development | In use |
| Spring Security | Authentication and authorization | Implemented |
| Spring Data JPA | Database interaction and repositories | In use |
| Hibernate | ORM and entity management | In use |
| Maven | Build and dependency management | In use |
| PostgreSQL | Relational database | In use |
| JWT | Stateless authentication | Implemented |
| BCrypt | Password hashing | Implemented |
| Jakarta Validation | Request validation | In use |

### Important

- Use **Java 21**.
- Use the existing Maven Wrapper rather than assuming Maven is globally installed.
- The project uses Spring Boot. Do not migrate frameworks or upgrade major versions without first inspecting `pom.xml`.
- Do not introduce additional frameworks or dependencies without a clear requirement.

## 2.2 Database

**Database:** PostgreSQL

**Database name:**

`mediscript_db`

**Local connection:**

- Host: `localhost`
- Port: `5432`
- Username: `postgres`
- Database: `mediscript_db`

The password is stored in an environment variable and must not be committed to Git.

### Current Hibernate configuration

The project currently uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update


This allows Hibernate to update the database schema based on entity changes during development.

Do not casually change this setting. A production deployment should eventually use controlled database migrations rather than relying on automatic schema updates.
2.3 Authentication Architecture
Authentication uses JWT.

The login endpoint is:

POST /api/auth/login

The JWT contains:
- sub: User email
- userId: User database ID
- role: User role
The JWT service uses:
- HMAC signing.
- Keys.hmacShaKeyFor(...).
- StandardCharsets.UTF_8.
- Issued-at timestamp.
- Expiration timestamp.
The JWT authentication filter:
1. Extracts the JWT from the request.
2. Validates the token.
3. Extracts the email from the subject.
4. Loads the corresponding user.
5. Checks that the user is active.
6. Creates an authenticated Spring Security principal.
7. Assigns the authority using the ROLE_<role> naming convention.
For example:
- ADMIN becomes ROLE_ADMIN.
- DOCTOR becomes ROLE_DOCTOR.
- PATIENT becomes ROLE_PATIENT.
The application uses method-level authorization through annotations such as:
@PreAuthorize("hasRole('ADMIN')")


and:
@PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")


The exact security configuration should be inspected before changing authorization behavior.
2.4 JWT Configuration
The actual configuration file is:

src/main/resources/application.yaml

Current configuration:
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


Important environment variables:
- DB_PASSWORD
- JWT_SECRET
JWT expiration is currently configured directly in YAML:

86400000 milliseconds = 24 hours.

There is no requirement to configure JWT_EXPIRATION separately unless the YAML configuration is changed.

Never ask the user to share actual passwords, JWT secrets, or authentication tokens.
2.5 Third-Party APIs and External Services
No external third-party business API integration has been confirmed as implemented in MediScript.

The current backend primarily uses:
- PostgreSQL.
- Spring Boot/Spring Security.
- JWT-based authentication.
- Maven-managed Java libraries.
Do not assume that payment gateways, SMS services, email services, cloud storage, external medical APIs, or other integrations have already been implemented.

3. Project Structure & Domain Architecture
The Java base package is:

com.mediscript.backend

The project follows a feature/module-oriented package structure.

Known module packages include:
com.mediscript.backend
│
├── auth
│   ├── controller
│   ├── service
│   ├── security
│   └── ...
│
├── user
│   ├── controller
│   ├── service
│   ├── entity
│   └── repository
│
├── doctor
│   ├── controller
│   ├── service
│   ├── entity
│   ├── repository
│   └── dto
│
├── patient
│   ├── controller
│   ├── service
│   ├── entity
│   ├── repository
│   └── dto
│
├── appointment
│   ├── controller
│   ├── service
│   ├── entity
│   ├── repository
│   └── dto
│
├── prescription
│   ├── controller
│   ├── service
│   ├── entity
│   ├── repository
│   └── dto
│
├── admin
│   ├── controller
│   └── service
│
├── audit
│   ├── entity
│   ├── repository
│   └── service
│
└── ...


This is an indicative structure based on the modules discussed. Inspect the actual source tree before creating or moving packages.
3.1 Main Database Entities
The following entities/tables have been discussed or implemented:
Entity	Database table	Purpose
User	users	User account information
Role	roles	User roles
Doctor	doctors	Doctor profile information
Patient	patients	Patient profile information
Appointment	appointments	Appointment records
Prescription	prescriptions	Prescription records
PrescriptionItem	prescription_items	Individual prescription items
AuditLog	audit_logs	Administrative audit history

The actual entity relationships and column definitions must be checked in the source code before modifying the database model.

4. What Has Been Built So Far
4.1 User Management
Implemented functionality includes:
- User creation.
- User retrieval.
- User updates.
- User status management.
- Role-based user management.
- BCrypt password hashing.
- Active/inactive user handling.
The system supports four roles:
- ADMIN
- DOCTOR
- PATIENT
- RECEPTIONIST
User accounts are linked to role records.

Passwords are stored as BCrypt hashes rather than plain text.
4.2 Authentication
Implemented:
- JWT authentication.
- Login endpoint.
- JWT generation.
- JWT validation.
- JWT expiration handling.
- User identification through email.
- User ID and role claims.
- Active-user validation.
- Spring Security authentication context.
- Role-based authorization.
Confirmed endpoint
Method	Endpoint	Purpose
POST	/api/auth/login	Authenticate a user and return a JWT

The login response includes information such as:
- User ID
- Name
- Email
- Role
- JWT token
4.3 Doctor Module
The Doctor module supports:
- Doctor profile creation.
- Doctor discovery.
- Doctor listing.
- Individual doctor retrieval.
- Doctor profile retrieval by the authenticated doctor.
- Doctor profile updates.
Confirmed endpoint structure
Method	Endpoint	Access
POST	/api/doctors	ADMIN
GET	/api/doctors	PATIENT, DOCTOR, ADMIN
GET	/api/doctors/{id}	PATIENT, DOCTOR, ADMIN
GET	/api/doctors/me	DOCTOR
PUT	/api/doctors/me	DOCTOR

The exact request and response DTOs should be inspected before changing the API contract.
Important authorization decision
Doctors should be able to view and update their own doctor profiles.

Patients should be able to discover doctors.

Doctor profile creation is currently an administrative operation.
4.4 Patient Module
The Patient module has a basic implementation.
Patient entity
Package:

com.mediscript.backend.patient.entity

Entity:

Patient.java

Database table:

patients

Known fields:
Field	Type
id	Long
user	User
dateOfBirth	LocalDate
gender	String
bloodGroup	String
address	String
emergencyContact	String

The user field is a one-to-one relationship:
@OneToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;


The patient profile is linked to an existing User account.
PatientRepository
Currently contains:
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);

    boolean existsByUser(User user);
}


PatientService
Currently implements:
public Patient createPatient(
    Long userId,
    LocalDate dateOfBirth,
    String gender,
    String bloodGroup,
    String address,
    String emergencyContact
)


The method:
1. Finds the user by ID.
2. Checks that the user's role is PATIENT.
3. Checks whether a patient profile already exists.
4. Creates the patient profile.
5. Saves the entity.
It also contains:
public List<Patient> getAllPatients();

public Patient getPatientById(Long patientId);


Missing patient records result in a ResourceNotFoundException.
PatientController
Base route:

/api/patients

Currently implemented endpoint:
Method	Endpoint	Purpose
POST	/api/patients	Create a patient profile

The endpoint:
- Accepts a validated CreatePatientRequest.
- Calls the PatientService.
- Returns a PatientResponse.
- Uses HTTP 201 CREATED.
The controller currently has no method-level @PreAuthorize annotation on the patient creation endpoint.

This must be reviewed and secured.
CreatePatientRequest
Currently contains:
- userId
- dateOfBirth
- gender
- bloodGroup
- address
- emergencyContact
Validation includes:
- @NotNull
- @Positive
- @Past
- @NotBlank
- @Size
PatientResponse
Contains:
- Patient ID
- Linked User ID
- Name
- Email
- Phone
- Date of birth
- Gender
- Blood group
- Address
- Emergency contact
The name, email, and phone are obtained from the linked User entity.
Important patient authorization decision
The project previously established that:

Doctors should also be able to create patient profiles for patients they treat.

This requirement must not be forgotten.

Doctors should be able to manage profiles belonging to patients under their care.

However, doctors must not be allowed to access arbitrary patient records.

The exact mechanism for proving a doctor-patient relationship is still being examined.

Potentially, the relationship can be established through appointments, but this must be checked against the existing Appointment entity and workflow.

Do not implement unrestricted doctor access merely by adding:
@PreAuthorize("hasRole('DOCTOR')")


to patient endpoints.
4.5 Appointment Module
Appointment functionality has already been implemented and tested.

Implemented features include:
- Appointment creation.
- Appointment retrieval.
- Patient appointment listing.
- Doctor appointment listing.
- Appointment status updates.
- Appointment ownership validation.
- Duplicate appointment-slot protection.
- Appointment lifecycle validation.
- Audit logging for appointment creation and status changes.
Confirmed endpoint structure
Method	Endpoint	Access
POST	/api/appointments	PATIENT, ADMIN
GET	/api/appointments/my	PATIENT, ADMIN
GET	/api/appointments/doctor	DOCTOR
PUT	/api/appointments/{id}/status	DOCTOR

Appointment lifecycle
The implemented lifecycle includes:
PENDING
   |
   |----> CONFIRMED
   |          |
   |          |----> COMPLETED
   |          |
   |          |----> CANCELLED
   |
   |----> CANCELLED


Supported transitions:
- PENDING → CONFIRMED
- PENDING → CANCELLED
- CONFIRMED → COMPLETED
- CONFIRMED → CANCELLED
Invalid status transitions are rejected.
Appointment authorization
The service checks whether a doctor owns the appointment before allowing doctor-side status updates.

Duplicate-slot protection has been implemented.

The exact Appointment entity and repository methods have not yet been re-reviewed in the latest development session.

They must be inspected before implementing doctor-patient relationship checks.
4.6 Prescription Module
Prescription functionality has been implemented.

Main entities:
- Prescription
- PrescriptionItem
The prescription module supports:
- Prescription creation.
- Prescription items.
- Doctor-side prescription creation.
- Patient-side prescription retrieval.
- Administrative prescription visibility.
Confirmed endpoint structure
Method	Endpoint	Access
POST	/api/prescriptions	DOCTOR
GET	/api/prescriptions/my	PATIENT, ADMIN
GET	/api/prescriptions/doctor	DOCTOR

Prescription creation is restricted to doctors.

The project has successfully tested prescription creation.

The exact DTOs, item fields, validation rules, and entity relationships should be read from the source code before extending this module.
4.7 Admin Module
Administrative functionality has been implemented.

The Admin module supports:
- User oversight.
- Doctor oversight.
- Patient oversight.
- Appointment oversight.
- Prescription oversight.
- Dashboard statistics.
- Audit log retrieval.
Confirmed Admin endpoints
Method	Endpoint	Purpose
GET	/api/admin/users	View users
GET	/api/admin/doctors	View doctors
GET	/api/admin/patients	View patients
GET	/api/admin/appointments	View appointments
GET	/api/admin/prescriptions	View prescriptions
GET	/api/admin/dashboard	Dashboard statistics
GET	/api/admin/audit-logs	Retrieve audit logs

The dashboard provides administrative statistics.

Admin oversight endpoints are intended for ADMIN users.

Doctor and patient accounts should receive HTTP 403 when attempting to access restricted Admin endpoints.

Admin oversight is primarily read-oriented for the clinical records listed above.

Do not assume that Admin should be able to create prescriptions. Prescription creation remains DOCTOR-only.
4.8 Audit Logging
Audit logging has been implemented.

Entity/table:
- AuditLog
- audit_logs
The audit system tracks important application actions.

Confirmed event types:
USER_CREATED
USER_STATUS_CHANGED
USER_UPDATED
APPOINTMENT_CREATED
APPOINTMENT_STATUS_CHANGED
PRESCRIPTION_CREATED


The audit design uses:
- A separate AuditLog entity.
- A separate audit_logs table.
- Actor identification using the authenticated user's email.
- SecurityContext to identify the current actor.
- UserRepository to resolve the actor's user record.
Privacy decision
Audit logs should not store sensitive clinical content such as:
- Diagnosis details.
- Medicine details.
- Prescription notes.
- Other unnecessary medical information.
The audit system should record the fact that an action occurred, who performed it, and relevant metadata without duplicating sensitive clinical content.

The audit functionality is read-only from the Admin API.

Potential future enhancement:
- Add patient-profile creation and update audit events if appropriate.
- Do this consistently with the existing audit design.
Do not add clinical content to audit metadata.

5. Active API Route Summary
The following is the consolidated route inventory based on the implementation discussed so far.

Some request/response details may need to be checked in the actual controller files.
Authentication
Method	Endpoint	Description
POST	/api/auth/login	Login and receive JWT

Doctors
Method	Endpoint	Access
POST	/api/doctors	ADMIN
GET	/api/doctors	PATIENT, DOCTOR, ADMIN
GET	/api/doctors/{id}	PATIENT, DOCTOR, ADMIN
GET	/api/doctors/me	DOCTOR
PUT	/api/doctors/me	DOCTOR

Patients
Method	Endpoint	Current status
POST	/api/patients	Implemented; authorization needs review

Planned patient endpoints, not yet confirmed as implemented:
Method	Endpoint	Intended purpose
GET	/api/patients/me	Retrieve authenticated patient's own profile
PUT	/api/patients/me	Update authenticated patient's own profile
GET	/api/patients/{id}	Retrieve a specific patient with authorization
PUT	/api/patients/{id}	Update a patient profile with authorization

A possible patient self-creation endpoint was discussed:

POST /api/patients/me

However, patient self-creation was not conclusively established as a final requirement.

Do not implement it as an already-agreed feature without discussing the intended workflow.
Appointments
Method	Endpoint	Access
POST	/api/appointments	PATIENT, ADMIN
GET	/api/appointments/my	PATIENT, ADMIN
GET	/api/appointments/doctor	DOCTOR
PUT	/api/appointments/{id}/status	DOCTOR

Prescriptions
Method	Endpoint	Access
POST	/api/prescriptions	DOCTOR
GET	/api/prescriptions/my	PATIENT, ADMIN
GET	/api/prescriptions/doctor	DOCTOR

Admin
Method	Endpoint	Access
GET	/api/admin/users	ADMIN
GET	/api/admin/doctors	ADMIN
GET	/api/admin/patients	ADMIN
GET	/api/admin/appointments	ADMIN
GET	/api/admin/prescriptions	ADMIN
GET	/api/admin/dashboard	ADMIN
GET	/api/admin/audit-logs	ADMIN


6. Known Issues & Unfinished Tasks
This section is particularly important. These are the areas that must be addressed before considering the backend complete.
6.1 Patient Module Authorization — High Priority
The current PatientController has a POST endpoint for patient profile creation.

It does not currently have a method-level authorization annotation.

The endpoint must be secured.

The previous design discussion established:
- ADMIN can create/manage patient profiles.
- DOCTOR can create/manage profiles for patients under their care.
- PATIENT should have access to their own profile, subject to the agreed self-service workflow.
However, the implementation must enforce these permissions at the service/business-logic level as well as at the controller level.

A role annotation alone is not sufficient to establish patient ownership.
6.2 Doctor-Patient Relationship Validation — High Priority
Doctors must not be able to access arbitrary patient records.

The intended security rule is:

A doctor can access or update a patient's profile only when that patient is under the doctor's care.

An appointment relationship may be used to establish this association.

Before implementation, inspect:
1. Appointment.java
2. AppointmentRepository.java
3. Doctor.java
4. DoctorRepository.java
These files were requested in the previous development session but had not yet been provided for review.

Determine:
- Whether Appointment references Patient directly.
- Whether Appointment references Doctor directly.
- Whether either relationship is through User.
- Which appointment statuses establish an active or valid care relationship.
- Whether a doctor can register a new patient before an appointment exists.
Do not invent Spring Data repository method names before examining the actual entity fields.

Possible approaches include a repository existsBy... method or a dedicated service-level ownership check, depending on the existing entity structure.
6.3 Doctor Creation of Patient Profiles — Workflow Decision Required
The user explicitly stated that doctors should be able to create patient profiles for patients they treat.

There is an unresolved workflow question:

How should a doctor establish the relationship with a patient when creating the patient's profile for the first time?

If the system requires an existing appointment to prove the relationship, it may prevent a doctor from registering a new patient before an appointment exists.

The next developer/LLM should inspect the existing appointment workflow and discuss this edge case before implementing the final validation.

Do not silently change the agreed requirement by making patient creation ADMIN-only.
6.4 Patient Self-Service — Partially Defined
The following were discussed as potential features:
- Retrieve own profile.
- Update own profile.
- Possibly create own profile.
The GET and PUT self-service endpoints are reasonable planned next steps, but their final behavior and creation workflow need to be confirmed.

The patient identity should be derived from the authenticated JWT principal, not from a user-supplied userId.

The authentication principal uses the user's email as its name.

A likely implementation approach is:
1. Read the authenticated email from Authentication.
2. Find the corresponding User.
3. Find the Patient profile linked to that User.
4. Return or update only that profile.
Never trust a request body containing another user's ID for a /me endpoint.
6.5 Patient Profile Update DTO — Not Implemented
There is currently no confirmed UpdatePatientRequest implementation.

A dedicated DTO should be introduced for profile updates.

Potential editable fields:
- Date of birth.
- Gender.
- Blood group.
- Address.
- Emergency contact.
The update request should not allow changing:
- Patient ID.
- Linked User ID.
- User role.
- User account status.
- Email or other account identity fields without a separate user-management workflow.
The exact validation rules should remain consistent with the existing CreatePatientRequest.
6.6 Patient Retrieval and Update Authorization — Not Implemented
Planned functionality:
- Patient retrieves own profile.
- Patient updates own profile.
- Admin retrieves/updates patient profiles.
- Doctor retrieves/updates only patients under their care.
Every doctor-side operation must validate the doctor-patient relationship.
6.7 Patient Audit Events — Not Yet Implemented
Current audit event types include user, appointment, and prescription actions.

Consider adding:
PATIENT_PROFILE_CREATED
PATIENT_PROFILE_UPDATED


Only add these if consistent with the project's audit requirements.

Do not log sensitive clinical content.
6.8 Test Coverage
The project has been tested through Maven and manual PowerShell API requests.

However, the next developer should inspect the existing test suite and determine whether automated tests cover:
- Patient profile creation.
- Duplicate patient profile prevention.
- Invalid patient role.
- Patient self-access.
- Doctor access to assigned patients.
- Doctor access to unrelated patients.
- Admin access.
- Unauthorized access.
- Appointment ownership.
- Prescription ownership.
- Invalid appointment status transitions.
- Audit event creation.
Do not assume every one of these scenarios already has an automated test.

7. Next Immediate Milestones
The next work should begin from the current patient module checkpoint rather than restarting completed modules.
Milestone 1: Inspect Appointment and Doctor Relationships
First obtain and review:
Appointment.java
AppointmentRepository.java
Doctor.java
DoctorRepository.java


Understand the exact entity relationships.

Determine how the system can establish that a particular patient belongs to a particular doctor's care.

This is a prerequisite for implementing secure doctor-side patient access.
Milestone 2: Finalize Patient Access Rules
Implement the agreed role and ownership model.

Expected access model:
Action	ADMIN	DOCTOR	PATIENT
Create patient profile	Yes	Yes, for patients under their care	Workflow needs confirmation
View own profile	Not applicable	Not applicable	Yes
Update own profile	Not applicable	Not applicable	Yes
View arbitrary patient	Yes	No	No
View patient under doctor's care	Yes	Yes	Own profile only
Update patient under doctor's care	Yes	Yes	Not applicable

The exact meaning of "under their care" must be enforced by backend logic.
Milestone 3: Secure Patient Creation
Update:
- PatientController
- PatientService
- Possibly PatientRepository
- Relevant DTOs
Ensure:
- Only permitted roles can create patient profiles.
- Target user must have the PATIENT role.
- Duplicate profiles are rejected.
- Doctor-side creation follows the finalized relationship workflow.
- A doctor cannot register or manipulate arbitrary users as patients.
Milestone 4: Implement Patient Self-Service
Potential endpoints:
GET /api/patients/me
PUT /api/patients/me


The authenticated user's identity must be derived from the security context.

Create an UpdatePatientRequest DTO.

Implement corresponding service methods.
Milestone 5: Implement Authorized Patient Retrieval and Updates
Potential endpoints:
GET /api/patients/{id}
PUT /api/patients/{id}


Access:
- ADMIN: authorized to manage patient profiles.
- DOCTOR: only patients under their care.
- PATIENT: cannot use these endpoints to access other patients.
Use explicit service-level ownership checks.
Milestone 6: Add Audit Logging for Patient Profile Changes
If included in the final audit requirements, add patient profile creation and update events.

Preserve the existing privacy-conscious audit design.
Milestone 7: Run Tests and Verify API Behavior
Run:
.\mvnw.cmd clean test


Expected result:
BUILD SUCCESS


Then manually test the relevant API endpoints using PowerShell and JWT authentication.

Verify both successful and forbidden requests.
Milestone 8: Review Overall Backend Completeness
Once the patient module is secure, review the backend for:
- Missing CRUD operations.
- Authorization gaps.
- Input validation.
- Exception handling.
- Database relationship integrity.
- Audit coverage.
- API response consistency.
- Automated test coverage.
Only after the backend is stable should major frontend integration or deployment work become the primary focus.

8. Coding Guidelines & Architecture Decisions
These decisions should be preserved when continuing development.
8.1 Java and Spring Conventions
- Use Java 21.
- Follow standard Java naming conventions.
- Classes: PascalCase.
- Methods and variables: camelCase.
- Constants: UPPER_SNAKE_CASE.
- Use constructor injection.
- Keep controllers focused on HTTP request/response handling.
- Keep business logic inside services.
- Keep database operations inside repositories.
- Use DTOs for API requests and responses.
- Avoid returning JPA entities directly from controllers.
- Use Jakarta validation annotations for request validation.
8.2 Package Organization
Prefer feature-based package organization.

For example:
patient/
    controller/
    service/
    entity/
    repository/
    dto/


Follow the existing package conventions rather than introducing a new architecture without a reason.
8.3 Dependency Injection
Use constructor injection.

Do not introduce field injection with @Autowired unless there is a compelling reason consistent with the existing codebase.
8.4 DTO Pattern
Use separate request and response DTOs.

Examples:
- CreatePatientRequest
- PatientResponse
- UpdatePatientRequest (planned)
Do not expose internal entity relationships or sensitive fields in API responses.
8.5 Validation
Use Jakarta Bean Validation.

Existing examples include:
@NotNull
@Positive
@Past
@NotBlank
@Size(max = 500)


Validate incoming data at the API boundary and enforce important business rules in the service layer.
8.6 Exception Handling
Existing application exceptions include:
- ResourceNotFoundException
- BadRequestException
Examples:
- Missing user → ResourceNotFoundException.
- Duplicate patient profile → BadRequestException.
- User has the wrong role → BadRequestException.
Follow the existing exception-handling conventions.

Inspect the global exception handler before introducing new exception types or response formats.
8.7 Security and Authorization
This is one of the most important project requirements.
- Use role-based authorization.
- Use @PreAuthorize where appropriate.
- Validate resource ownership in service methods.
- Do not trust user IDs supplied by clients when identity can be derived from the JWT.
- Do not allow doctors to access all patients.
- Do not allow patients to retrieve another patient's profile.
- Validate appointment ownership before doctor-side status changes.
- Keep prescription creation restricted to doctors.
- Keep Admin endpoints restricted to ADMIN.
- Check active-user status during JWT authentication.
Authorization must be enforced server-side.

Hiding frontend buttons is not a security mechanism.
8.8 Audit Logging
Continue using a separate audit log entity/table.

Use the authenticated user from the SecurityContext to determine the actor.

Avoid logging unnecessary sensitive medical information.

Keep audit retrieval restricted to Admin.
8.9 API Response Conventions
Follow the existing HTTP response conventions.

Examples:
- 200 OK for successful retrieval/update.
- 201 CREATED for successful creation.
- 400 BAD REQUEST for invalid requests.
- 401 UNAUTHORIZED for missing/invalid authentication.
- 403 FORBIDDEN for insufficient permissions.
- 404 NOT FOUND for missing resources.
Do not change existing response formats without considering frontend compatibility.
8.10 Database and Entity Relationships
- Use JPA relationships where appropriate.
- Preserve existing foreign-key relationships.
- Avoid duplicate patient profiles for the same user.
- Avoid changing entity mappings without checking existing data.
- Be cautious with lazy-loaded relationships and JSON serialization.
- Do not casually introduce cascade operations that could delete linked user records.
The Patient entity currently has a unique one-to-one relationship with User.
8.11 Incremental Development
After each meaningful implementation step:
.\mvnw.cmd clean test


Do not continue building additional features on top of a failing build.

When debugging:
1. Ask for the complete relevant error.
2. Inspect the actual code.
3. Identify the root cause.
4. Make the smallest appropriate fix.
5. Re-run tests.
Avoid speculative changes across multiple modules at once.

9. Development Environment Setup
9.1 Operating System
Windows.
9.2 IDE
IntelliJ IDEA.
9.3 Java
Installed Java version:
OpenJDK 21.0.12 LTS
Eclipse Temurin


Verify:
java -version


9.4 Maven
The project uses Maven Wrapper.

Maven version previously verified:
Apache Maven 3.9.16


Use:
.\mvnw.cmd -version


Do not assume that the global mvn command exists.
9.5 PostgreSQL
Local PostgreSQL server:
localhost:5432


Database:
mediscript_db


Ensure PostgreSQL is running before starting the Spring Boot backend.
9.6 Project Directory
Previously used project root:
C:\Users\Subhayu Ghosal\PROJECTS\mediscript\backend\mediscript-backend


The directory may differ on another machine. Inspect the current working directory instead of assuming it.
9.7 Useful Commands
Check Java:
java -version


Check Maven Wrapper:
.\mvnw.cmd -version


Clean and test:
.\mvnw.cmd clean test


Start backend:
.\mvnw.cmd spring-boot:run


Stop backend:
Ctrl + C


Check Git status:
git status


Check current branch:
git branch --show-current


Check recent commits:
git log --oneline -10



10. Manual API Testing with PowerShell
The project has been tested using PowerShell and Invoke-RestMethod.

Example login:
$loginBody = @{
    email = "admin@mediscript.local"
    password = "<YOUR_ADMIN_PASSWORD>"
} | ConvertTo-Json

$adminLogin = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginBody


Extract token:
$token = $adminLogin.token

$headers = @{
    Authorization = "Bearer $token"
}


Example Admin API call:
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/admin/users" `
    -Method GET `
    -Headers $headers


Example audit log request:
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/admin/audit-logs" `
    -Method GET `
    -Headers $headers


Never paste actual passwords or JWT tokens into project documentation, GitHub, or another LLM.

Use placeholders when sharing commands.

11. Testing and Verification History
The following has been successfully verified during previous development sessions.
11.1 Build Verification
Command:
.\mvnw.cmd clean test


Previously returned:
BUILD SUCCESS


This was verified during the November 5, 2026 session.
11.2 Backend Startup
The backend successfully started using:
.\mvnw.cmd spring-boot:run


Server:
http://localhost:8080


11.3 Root Endpoint
A request to:
http://localhost:8080


returned HTTP 403.

This was expected because the root endpoint is not configured as a public endpoint.

Do not disable Spring Security just to make the root URL return HTTP 200.
11.4 Admin Login
The Admin login endpoint worked successfully.

Previously tested Admin account:
admin@mediscript.local


The actual password is intentionally not recorded here.

The login returned:
- User ID: 9
- Name: System Administrator
- Role: ADMIN
- JWT token
11.5 Admin API Testing
The following worked successfully:
GET /api/admin/users
GET /api/admin/audit-logs


The Admin user list contained Admin, Doctor, and Patient accounts.

The audit log endpoint returned eight records during that test.

The audit records included the expected core event types.
11.6 Appointment and Prescription Testing
Previously tested successfully:
- Appointment creation.
- Appointment confirmation.
- Appointment completion.
- Prescription creation.
- Appointment ownership validation.
- Duplicate-slot protection.
- Admin oversight endpoints.
- Audit event creation.
These modules should not be rebuilt from scratch.

12. Git History and Checkpoints
The project uses Git and the main branch.

Previously recorded commits include:
ff06a11 Add audit logging for core clinical actions
d6867ca Add admin dashboard statistics
823e2f7 Add admin management and oversight endpoints
51c25d4 Add admin user management
a0fbadc Refine authorization and request security


At the November 5 checkpoint:
- The current branch was main.
- The working tree was clean.
- The local branch was two commits ahead of origin/main.
This is a historical checkpoint, not a guarantee of the current Git status.

Always run:
git status
git log --oneline -10


before modifying or committing code.

A checkpoint document has previously been used:
MEDISCRIPT_PROJECT_STATE.md


Check whether it exists in the current repository and update it when reaching a meaningful development milestone.

13. Important Decisions and Requirements to Preserve
These are particularly important because they were explicitly discussed during development.
13.1 Roles
The application has four roles:
ADMIN
DOCTOR
PATIENT
RECEPTIONIST


Do not remove or rename these roles without discussing the impact on the existing database and authorization logic.
13.2 Doctor-Patient Relationship
Doctors should be able to create and manage profiles for patients they treat.

They should not have unrestricted access to every patient in the system.

The relationship should be verified through a suitable backend relationship, potentially using appointments.
13.3 Patient Identity
Patient profiles are linked to User accounts.

The current Patient entity uses a one-to-one relationship with User.

Do not create duplicate patient profiles for one user.
13.4 Prescription Permissions
Prescription creation is doctor-only.

Admin oversight does not automatically mean Admin should be allowed to create prescriptions.
13.5 Appointment Ownership
Doctors may update the status of appointments assigned to them.

The service must verify appointment ownership.
13.6 Audit Privacy
Audit logs should not duplicate diagnosis, medicine, or prescription note content.
13.7 Existing Authentication
The JWT subject is the user's email.

The token also includes the user ID and role.

Do not change this authentication design without reviewing all dependent code.
13.8 Development Style
The user prefers:
- Step-by-step explanations.
- Small implementation milestones.
- Actual code inspection before changes.
- Complete file-level code when modifications are required.
- Build verification after meaningful changes.
- Clear explanations of why a change is necessary.
Avoid dumping an entire new backend implementation in one response.

14. Immediate Handoff Instructions for the Next LLM
Continue from the existing MediScript implementation.

Do not restart the project or recreate completed functionality.

The next task is:

Continue securing and completing the Patient module.

The first action should be to request or inspect these four existing files:
Appointment.java
AppointmentRepository.java
Doctor.java
DoctorRepository.java


These files are needed to understand the existing relationship between doctors, patients, and appointments.

Then:
1. Explain the current entity relationship.
2. Determine how doctor-patient ownership should be verified.
3. Identify the correct patient creation workflow.
4. Secure the existing POST /api/patients endpoint.
5. Implement patient self-service retrieval and updates according to the finalized requirements.
6. Implement authorized doctor-side patient retrieval and updates.
7. Add tests for access control and ownership.
8. Run .\mvnw.cmd clean test.
9. Update MEDISCRIPT_PROJECT_STATE.md and Git checkpoint information.
Do not make these mistakes
- Do not make patient profile creation ADMIN-only.
- Do not forget that doctors should also create patient profiles for patients they treat.
- Do not assume patients definitely need a self-creation endpoint; this workflow was not conclusively finalized.
- Do not allow doctors to access arbitrary patients.
- Do not trust client-provided user IDs for /me endpoints.
- Do not rewrite completed appointment, prescription, authentication, or Admin modules.
- Do not change the JWT design unnecessarily.
- Do not expose passwords, tokens, or JWT secrets.
- Do not assume the current Git status without checking it.
- Do not assume every proposed route has already been implemented.

15. Current Project Status at a Glance
Module	Status	Notes
Project setup	Completed	Java 21, Spring Boot, Maven
PostgreSQL integration	Completed	mediscript_db
User management	Implemented	Admin management and user status
Role management	Implemented	ADMIN, DOCTOR, PATIENT, RECEPTIONIST
Password hashing	Implemented	BCrypt
JWT authentication	Implemented	Login, filter, token validation
Spring Security	Implemented	Role-based access
Doctor module	Implemented	Profile management and discovery
Patient module	Partially implemented	Creation and basic retrieval in service
Patient authorization	Needs work	Doctor-patient ownership validation
Appointment module	Implemented	Lifecycle and duplicate-slot protection
Prescription module	Implemented	Prescription creation and items
Admin module	Implemented	Oversight and dashboard
Audit logging	Implemented	Core clinical and user actions
Automated tests	Build verified	Coverage needs further review
Frontend integration	Not established in this checkpoint	Inspect repository before assuming status
Deployment	Not established in this checkpoint	No confirmed deployment configuration


16. Final Notes
MediScript is an existing, partially completed application with substantial backend functionality already implemented.

The priority is not to redesign the system. It is to continue development while preserving its current architecture, security model, and completed features.

The most important unfinished area is the Patient module's access-control model, particularly the relationship between doctors and patients.

Always inspect the actual source code before proposing changes. Historical documentation and this handoff describe the known implementation, but the current repository is the final source of truth.

Start from the Patient module authorization checkpoint and continue incrementally.