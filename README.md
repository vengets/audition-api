# Audition API

The purpose of this Spring Boot application is to test general knowledge of SpringBoot, Java, Gradle etc. It is created
for hiring needs of our company but can be used for other purposes.

## Overarching expectations & Assessment areas

<pre>
This is not a university test. 
This is meant to be used for job applications and MUST showcase your full skillset. 
<b>As such, PRODUCTION-READY code must be written and submitted. </b> 
</pre>

- clean, easy to understand code
- good code structures
- Proper code encapsulation
- unit tests with minimum 80% coverage.
- A Working application to be submitted.
- Observability. Does the application contain Logging, Tracing and Metrics instrumentation?
- Input validation.
- Proper error handling.
- Ability to use and configure rest template. We allow for half-setup object mapper and rest template
- Not all information in the Application is perfect. It is expected that a person would figure these out and correct.

## Getting Started

### Prerequisite tooling

- Any Springboot/Java IDE. Ideally IntelliJIdea.
- Java 17
- Gradle 8

### Prerequisite knowledge

- Java
- SpringBoot
- Gradle
- Junit

### Importing Google Java codestyle into INtelliJ

```
- Go to IntelliJ Settings
- Search for "Code Style"
- Click on the "Settings" icon next to the Scheme dropdown
- Choose "Import -> IntelliJ Idea code style XML
- Pick the file "google_java_code_style.xml" from root directory of the application
__Optional__
- Search for "Actions on Save"
    - Check "Reformat Code" and "Organise Imports"
```

---
**NOTE** -
It is highly recommended that the application be loaded and started up to avoid any issues.

---

## Audition Application information

This section provides information on the application and what the needs to be completed as part of the audition
application.

The audition consists of multiple TODO statements scattered throughout the codebase. The applicants are expected to:

- Complete all the TODO statements.
- Add unit tests where applicants believe it to be necessary.
- Make sure that all code quality check are completed.
- Gradle build completes sucessfully.
- Make sure the application if functional.

## Submission process

Applicants need to do the following to submit their work:

- Clone this repository
- Complete their work and zip up the working application.
- Applicants then need to send the ZIP archive to the email of the recruiting manager. This email be communicated to the
  applicant during the recruitment process.

  
---

## 📘 Additional Information Based on the Implementation

### ✅ Overview

The **Audition API** is a RESTful Spring Boot application that exposes endpoints to manage and retrieve posts and their
associated comments. It demonstrates core Spring Boot concepts such as dependency injection, request handling, exception
management, validation, and testing.

---

### 🔧 Key Implementation Decisions

- **Controller Design**:  
  The API is structured around RESTful principles using clean and meaningful endpoints like `/posts`, `/posts/{id}`, and
  `/posts/{id}/comments`.

- **Input Validation**:  
  Used Bean Validation annotations like `@Positive` on `@PathVariable` and `@RequestParam` to enforce input correctness
  and reduce manual checks.

- **Dependency Injection**:  
  Followed **constructor-based injection** for better testability and adherence to best practices.

- **Exception Handling**:  
  Centralized error handling using `@ControllerAdvice`, returning structured `ProblemDetail` responses for validation
  and system-level errors.

- **Logging**:  
  Adopted `@Slf4j` for cleaner logging across the controller and exception advice layers.

---

### 📈 Observability

- **Logging**: Enabled using SLF4J and `@Slf4j` for error, warning, and info logs.
- **Metrics and Tracing**: Not implemented in this version but can be easily added using Spring Boot Actuator and
  Micrometer.

---

### 🧪 Testing

- Used **`TestRestTemplate`** for integration tests with `@SpringBootTest(webEnvironment = RANDOM_PORT)`.
- Covered both **happy paths and error scenarios**, including validation failures.
- Ensured endpoint behavior through mock-based service layer injections.

> ✅ Current test coverage is over **80%**, satisfying the expected benchmark.

---

### 🧹 Code Quality & Standards

- Applied **Google Java Style Guide** using the provided `google_java_code_style.xml`.
- Ensured organized imports and automatic formatting on save in IntelliJ IDEA.
- Structured the code into meaningful packages (`web`, `model`, `service`, `common`, etc.) for better modularity.

---

### 📚 API Documentation (Swagger UI)

The application is now integrated with **Swagger (OpenAPI 3)** using the `springdoc-openapi` library.  
Once the application is running, the interactive API documentation can be accessed via:

```
http://localhost:8080/swagger-ui.html
```

or

```
http://localhost:8080/swagger-ui/index.html
```

This interface allows you to explore and test the available REST endpoints directly from the browser.

---

### 🚧 Limitations & Opportunities for Improvement

- **No persistence layer** (database integration).
- **No authentication/authorization** — adding Spring Security would be ideal in a production-ready API.
- **Observability** could be extended with tracing (e.g., OpenTelemetry) and metrics (Micrometer, Prometheus).

---

### 📦 Final Notes

- The application has been tested with Java 17 and Gradle 8.
- Build completes successfully with `./gradlew clean build`.
- To get the test report `./gradlew jacocoTestReport `.
- All TODOs were implemented, and additional enhancements were made where applicable.
