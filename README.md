# Student Registration System

An enterprise-grade **Student Registration System** built with **Java 17, Spring Boot 3, Spring MVC,
Spring Data JPA, Spring Security, MySQL, Thymeleaf, Bootstrap 5** and vanilla JavaScript.

---

## 1. Technology Stack

| Layer            | Technology                                   |
|------------------|-----------------------------------------------|
| Language         | Java 17                                       |
| Framework        | Spring Boot 3.2.5 (Spring MVC, Spring Security)|
| Persistence      | Spring Data JPA + Hibernate                   |
| Database         | MySQL 8                                       |
| Build Tool       | Maven                                         |
| View Layer       | Thymeleaf 3 + Thymeleaf Security extras       |
| UI               | Bootstrap 5, Bootstrap Icons, custom CSS/JS   |
| Boilerplate      | Lombok                                        |
| Validation       | Jakarta Bean Validation + custom validators   |

---

## 2. Project / Package Structure

```
student-registration-system/
├── pom.xml
├── src/main/java/com/enterprise/studentregistration/
│   ├── StudentRegistrationApplication.java     Main class
│   ├── config/
│   │   ├── SecurityConfig.java                 Spring Security rules, form login
│   │   └── WebMvcConfig.java                   Serves /uploads/** static photos
│   ├── controller/
│   │   ├── AuthController.java                 Login/forgot/reset/change password
│   │   ├── DashboardController.java            Dashboard stats page
│   │   ├── StudentController.java              Full student CRUD + search/sort/paging
│   │   ├── ProfileController.java               Student's own read-only profile
│   │   ├── HomeController.java / ErrorPageController.java
│   ├── service / service.impl/
│   │   ├── StudentService(Impl)                 Business logic for students
│   │   ├── UserService(Impl)                    Login/password business logic
│   │   ├── DashboardService(Impl)               Aggregate statistics
│   │   └── CustomUserDetailsService             Bridges User entity -> Spring Security
│   ├── repository/
│   │   ├── StudentRepository, UserRepository    Spring Data JPA repositories
│   │   └── StudentSpecifications                Dynamic search/filter criteria
│   ├── entity/
│   │   ├── Student, User, Role, Gender, StudentStatus
│   ├── dto/
│   │   ├── StudentDTO, ChangePasswordDTO, ForgotPasswordDTO,
│   │   │   ResetPasswordDTO, DashboardStatsDTO
│   ├── validation/
│   │   ├── UniqueEmail / UniqueEmailValidator   Duplicate email check
│   │   └── ValidMobile / ValidMobileValidator   10-digit mobile check
│   ├── exception/
│   │   ├── ResourceNotFoundException, DuplicateEmailException,
│   │   │   InvalidPasswordException, FileStorageException
│   │   └── GlobalExceptionHandler               @ControllerAdvice -> friendly error pages
│   └── util/
│       ├── StudentIdGenerator                   Generates STU2026001-style IDs
│       └── FileStorageUtil                      Handles photo upload storage
├── src/main/resources/
│   ├── application.properties
│   ├── db/schema.sql                            MySQL DDL + sample data
│   ├── static/css/style.css, static/js/script.js
│   └── templates/
│       ├── login.html, forgot-password.html, forgot-password-confirmation.html,
│       │   reset-password.html, change-password.html, dashboard.html
│       ├── fragments/header.html, sidebar.html, footer.html
│       ├── students/list.html, form.html, view.html
│       └── error/400.html, 403.html, 404.html, 500.html
```

### Why this structure?
- **Controller → Service → Repository → Entity** is the classic Spring layered
  architecture: controllers stay thin (routing + view selection), services hold
  business rules and transactions, repositories are pure data access.
- **DTOs** decouple the web-form layer from JPA entities — form validation rules
  (`@NotBlank`, `@Past`, custom `@ValidMobile`) live on the DTO, not the entity,
  so the entity stays a clean persistence model.
- **Custom validators** (`@UniqueEmail`, `@ValidMobile`) show how Bean Validation
  is extended for business-specific rules beyond the built-in annotations.
- **GlobalExceptionHandler** centralizes error handling so no controller needs
  try/catch blocks for expected failure cases (not-found, duplicate email, bad
  password, file upload errors).

---

## 3. Database Setup

1. Create MySQL 8+ instance, then run the provided script:
   ```bash
   mysql -u root -p < src/main/resources/db/schema.sql
   ```
   This creates the `student_registration_db` database, the `students` and
   `users` tables, sample students, and two demo login accounts.

2. Update credentials in `src/main/resources/application.properties` if your
   MySQL username/password differ from `root` / `root`.

### Demo accounts (from schema.sql)
| Role    | Username | Password     |
|---------|----------|---------------|
| Admin   | `admin`  | `Admin@123`   |
| Student | `jsmith` | `Student@123` |

---

## 4. Running the Project

### Option A — Command line (Maven)
```bash
cd student-registration-system
mvn spring-boot:run
```
Visit **http://localhost:8080** (redirects to `/login`).

### Option B — IntelliJ IDEA
1. **File → Open** and select the `student-registration-system` folder (the one with `pom.xml`).
2. IntelliJ auto-detects it as a Maven project and downloads dependencies.
3. Ensure **Project SDK** is set to Java 17 (`File → Project Structure → SDK`).
4. Open `StudentRegistrationApplication.java`, click the green ▶ run icon next
   to the `main` method (or right-click → Run).
5. Confirm MySQL is running and `application.properties` points to it.

### Option C — Eclipse
1. **File → Import → Maven → Existing Maven Projects**, browse to the project root, Finish.
2. Right-click project → **Properties → Java Build Path** → confirm JRE is 17.
3. If Lombok annotations aren't recognized, download `lombok.jar` from
   `https://projectlombok.org/download`, run it, point it at your Eclipse
   install, and restart Eclipse (enables `@Data`, `@Builder`, etc.).
4. Right-click `StudentRegistrationApplication.java` → **Run As → Spring Boot App**
   (or **Java Application** if the Spring plugin isn't installed).

### Option D — Spring Tool Suite (STS)
1. **File → Import → Maven → Existing Maven Projects**, select the project folder.
2. STS ships with Lombok support pre-integrated in recent versions; otherwise
   install it the same way as Eclipse above.
3. Right-click the project → **Run As → Spring Boot App**.
4. The **Boot Dashboard** view (bottom panel) also lets you start/stop/restart
   the app and view console logs.

---

## 5. Feature Walkthrough

- **Login Module** — Spring Security form login (`/login`), role-based access
  (`ADMIN` sees full CRUD + dashboard; `STUDENT` sees only their own profile),
  forgot-password (identify by username + email → one-time token → reset
  page), and change-password for authenticated users.
- **Student Registration / CRUD** — `/students/new` (create), `/students/{id}/edit`
  (update), `/students/{id}/delete` (delete via confirmation modal), `/students/{id}`
  (view). Student IDs are auto-generated (`STU2026001`) and never editable.
- **Dashboard** — total, active, male, and female student counts, plus a table
  of the 5 most recently registered students.
- **Search / Filter / Sort / Pagination** — all combinable on `/students`:
  keyword (ID/name/email), course, branch, semester, column sorting (click
  header links), and page-size-aware pagination controls.
- **Validation** — required fields, `@Email`, custom 10-digit `@ValidMobile`,
  6-digit pin code regex, and duplicate-email prevention (`@UniqueEmail`,
  correctly excluding the record being edited).
- **File Upload** — student photo stored under `uploads/student-photos/` on
  disk (path configurable via `app.upload.dir`), served at `/uploads/**`, with
  a live JS preview on the form before submission.

---

## 6. Interview Questions & Answers

**Q1. Why separate DTOs from entities instead of using the entity directly in the controller?**
A. DTOs decouple the persistence model from the web-form contract. It lets us
apply web-specific validation (e.g., a `MultipartFile photo` field that never
touches the database directly), avoid over-posting attacks (a malicious client
can't set fields like `createdAt` that aren't exposed on the DTO), and keep
JPA lazy-loading concerns away from Thymeleaf templates.

**Q2. How does Spring Data JPA generate the query behind `findByEmailIgnoreCase`?**
A. Spring Data parses the method name into a JPQL query at startup using its
naming convention parser: `findBy` + `Email` (property) + `IgnoreCase`
(case-insensitive comparison keyword). No implementation code is needed —
the interface is a proxy Spring creates at runtime.

**Q3. How would you prevent two admins from registering the same email at the same time (race condition)?**
A. The `@UniqueEmail` bean validation is a first-line, user-friendly check, but
it's not atomic. The real guarantee comes from the **unique constraint on the
`email` column** in MySQL (`uniqueConstraints` on `@Table`) — a concurrent
insert will fail at the database level and can be translated into a friendly
error via `DataIntegrityViolationException` handling in `GlobalExceptionHandler`.

**Q4. Why use `Specification<Student>` instead of multiple `findByXAndYAndZ` repository methods?**
A. The search page allows any *combination* of keyword, course, branch, and
semester filters — some present, some not. Writing a derived query for every
combination would explode combinatorially. `JpaSpecificationExecutor` lets us
build one dynamic predicate that only adds conditions for the filters that are
actually supplied.

**Q5. How does Spring Security know whether to show the Admin or Student menu?**
A. `CustomUserDetailsService` loads the `User` entity and converts its single
`Role` enum into a Spring Security `GrantedAuthority` string (`ROLE_ADMIN` or
`ROLE_STUDENT`). Both `SecurityConfig` (`hasRole("ADMIN")`) and the Thymeleaf
templates (`sec:authorize="hasRole('ADMIN')"`) read this authority to
authorize URLs and conditionally render UI.

**Q6. Where is the password actually verified during login, and what encoder is used?**
A. `AuthenticationManager` (auto-configured by Spring Security) calls
`CustomUserDetailsService.loadUserByUsername()` to fetch the stored (BCrypt)
hash, then uses the `PasswordEncoder` bean (`BCryptPasswordEncoder`, defined in
`SecurityConfig`) to compare the raw submitted password against that hash via
`matches()`. Plaintext passwords are never compared directly or stored.

**Q7. Why is `@Transactional` placed on service methods rather than repository or controller methods?**
A. The service layer is where a "unit of work" boundary is defined — e.g.,
`updateStudent()` may re-validate email uniqueness, mutate several fields, and
replace an uploaded photo, all of which must succeed or fail together.
Repositories are too low-level (single queries), and controllers shouldn't own
transaction boundaries since that couples the web layer to persistence
concerns.

**Q8. How is the auto-generated Student ID guaranteed to be unique and sequential?**
A. `StudentIdGenerator` queries the last student ID created for the *current
year* (`STU2026%`) ordered descending, parses the numeric suffix, and
increments it. The method is `synchronized` to reduce (though not fully
eliminate under extreme concurrency) race conditions; in a high-throughput
production system, a database sequence or a dedicated counter table with
pessimistic locking would be a more robust choice.

**Q9. What happens if someone uploads a 10MB photo or a `.exe` file as a student photo?**
A. `FileStorageUtil.storePhoto()` explicitly checks `file.getContentType()`
against an allow-list (`image/jpeg`, `image/png`, `image/webp`) and rejects
anything else, and checks `file.getSize()` against a 5MB cap — both are
enforced server-side (never trust client-side `accept="image/*"` alone), and
`FileStorageException` is caught by `GlobalExceptionHandler` to show a clean
error instead of a stack trace.

**Q10. How would you scale the "recently registered students" dashboard query if the students table had millions of rows?**
A. `findRecentlyRegistered()` already uses `Pageable` (limit 5) rather than
loading everything, and `created_at` has a dedicated index (`idx_students_created_at`)
in `schema.sql`. At larger scale, this could be moved to a materialized
view/cache refreshed periodically, or the dashboard stats could be served from
a read replica to avoid contending with write-heavy CRUD traffic.

**Q11. Why does `StudentDTO`'s `@UniqueEmail` annotation sit at the class level instead of on the `email` field?**
A. Field-level constraints only receive the single field's value, but correctly
skipping the "duplicate" check for the record currently being edited requires
seeing **both** the DTO's `id` and its `email` at once. A class-level
constraint receives the whole DTO object, so `UniqueEmailValidator` can
compare the found record's id against `dto.getId()`.

**Q12. What's the difference between `spring.jpa.hibernate.ddl-auto=update` (used here) and using the provided `schema.sql` directly?**
A. `ddl-auto=update` lets Hibernate auto-adjust tables to match entity changes
during development — convenient, but risky in production because it can
silently alter schema. The provided `schema.sql` is meant to be run manually
against a fresh database for a reproducible baseline (with sample data);
in a real production pipeline you'd disable `ddl-auto` entirely and use a
migration tool like Flyway or Liquibase instead.

---

## 7. Notes & Possible Extensions

- Add Flyway/Liquibase for versioned schema migrations instead of `ddl-auto=update`.
- Add pagination page-size selector and CSV/Excel export on the student list.
- Add real email delivery (Spring Mail) for the forgot-password token instead
  of displaying it directly on the confirmation page (that's done here purely
  for demo convenience without an SMTP server).
- Add integration tests with `@SpringBootTest` + Testcontainers (MySQL) for
  the repository and service layers.
