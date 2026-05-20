# Student Attendance Management System

A file-based Student Attendance Management System built with **Java 21**, **Spring Boot**, and **Thymeleaf**. Teachers or admins upload a CSV of students, mark attendance in a web UI, and export a timestamped attendance CSV stored locally—no database required.

## Project Overview

| Step | Action |
|------|--------|
| 1 | Upload a CSV with student records (`cmsId`, `name`, `fatherName`) |
| 2 | Review students on an attendance sheet (default: **Absent**) |
| 3 | Mark **Present** or **Absent** and click **Save Attendance** |
| 4 | System writes `attendance_yyyy_MM_dd_HH_mm.csv` under `attendance-records/` |
| 5 | Download the generated file from the success page or attendance sheet |

## Technologies Used

- Java 21+
- Spring Boot 3.4 (Web, Thymeleaf, Validation)
- Maven
- Lombok
- OpenCSV 5.9
- Bootstrap 5.3
- SLF4J (via Spring Boot logging)

## Prerequisites

- **JDK 21** or higher
- **Maven 3.9+** (or use the included `./mvnw` wrapper)

## Setup Steps

1. **Clone or open the project**
   ```bash
   cd attendance-practice
   ```

2. **Build the project**
   ```bash
   ./mvnw clean package
   ```
   On Windows:
   ```cmd
   mvnw.cmd clean package
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or run the JAR:
   ```bash
   java -jar target/attendance-practice-0.0.1-SNAPSHOT.jar
   ```

4. **Open in browser**
   ```
   http://localhost:8080
   ```

## How to Use

1. Go to the homepage and upload a `.csv` file (see format below).
2. On the **Attendance Sheet**, mark each student and click **Save Attendance**.
3. On the **success** page, use **Download Attendance CSV** to get the file.
4. Saved files are stored in the project root folder:
   ```
   attendance-records/attendance_2026_05_20_10_30.csv
   ```

A sample input file is provided at `samples/students.csv`.

## CSV Format Instructions

### Input (upload)

**Header (required):**
```csv
cmsId,name,fatherName
```

**Example:**
```csv
cmsId,name,fatherName
101,Ali Khan,Ahmed Khan
102,Sara Ali,Rashid Ali
```

### Output (generated after save)

**Header:**
```csv
cmsId,name,fatherName,attendance
```

**Example:**
```csv
cmsId,name,fatherName,attendance
101,Ali Khan,Ahmed Khan,Present
102,Sara Ali,Rashid Ali,Absent
```

## Configuration

Edit `src/main/resources/application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `app.attendance.records-directory` | Folder for saved attendance CSVs | `attendance-records` |
| `app.csv.expected-headers` | Required upload CSV headers | `cmsId,name,fatherName` |
| `spring.servlet.multipart.max-file-size` | Max upload size | `5MB` |

## Architecture

Layered, clean architecture under `com.example.attendance`:

```
controller/     → HTTP, thin controllers
service/        → Business contracts
service/impl/   → Business logic
model/          → Domain entities & enums
dto/            → Data transfer objects
utility/        → CSV read/write, file validation
exception/      → Custom exceptions + @ControllerAdvice
config/         → Properties, constants, startup config
```

**Design highlights**

- Constructor injection only
- Service interfaces for future DB/API swaps
- Session-based state (ready to replace with DB or JWT later)
- Centralized error handling with user-friendly messages
- OpenCSV isolated in utility classes

## Error Handling

The application handles:

- Empty or missing file uploads
- Non-`.csv` files
- Invalid or missing CSV headers
- Malformed rows (skipped with logging)
- Duplicate CMS IDs
- Empty student lists
- File read/write failures
- Session expiry (redirect to upload)

## Future Scalability Notes

The codebase is structured so you can extend it without major rewrites:

| Future feature | Approach |
|----------------|----------|
| **Database** | Replace `SessionService` / file storage with JPA repositories; keep service interfaces |
| **Authentication** | Add Spring Security; protect `/upload` and `/attendance` |
| **REST API** | Add `@RestController` alongside existing MVC controllers; reuse services |
| **Attendance history** | Persist records in DB; add query service and list UI |
| **Analytics** | New service over stored attendance data |
| **Multi-class support** | Extend `Student` with `classId`; filter sheets by class |
| **React/Angular frontend** | Expose REST endpoints; keep core logic in services |

## License

This project is for educational and practice purposes.
