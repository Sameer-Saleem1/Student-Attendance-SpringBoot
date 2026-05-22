# Project Overview

## What the application does
- Provides a file-based student attendance workflow using Spring Boot MVC and a REST API.
- Accepts a CSV upload with student records, shows an attendance sheet (default Absent), and saves a timestamped attendance CSV to a local folder.
- Stores the active student list and the last saved file name in the HTTP session to keep the flow simple without a database.

## Core workflow (UI and API)
1. Upload a CSV containing `cmsId,name,fatherName`.
2. Attendance sheet is generated with all students marked Absent by default.
3. Attendance marks are submitted and saved as `attendance_yyyy_MM_dd_HH_mm.csv` in `attendance-records/`.
4. Saved file can be downloaded from the success page or via the API.

## Key components
- Controllers:
  - `HomeController` serves the upload page.
  - `AttendanceController` serves the attendance sheet and success views.
  - `AttendanceApiController` exposes `/api/attendance` endpoints for upload, sheet retrieval, save, and download.
- Services:
  - `StudentService` validates and parses CSV uploads.
  - `AttendanceService` applies attendance marks, writes CSV output, and resolves saved files.
  - `SessionService` stores and retrieves session-scoped students and saved file names.
- Utilities:
  - `CsvReaderUtil` reads and validates CSV input, enforces headers, and ignores malformed rows.
  - `CsvWriterUtil` writes attendance CSV output and formats attendance values.
  - `FileValidationUtil` ensures upload is a non-empty `.csv`.
- Configuration:
  - `AppProperties` exposes configurable storage and CSV settings.
  - `FileStorageConfig` creates the output directory at startup.
- Error handling:
  - MVC: `GlobalExceptionHandler` shows friendly messages on the UI.
  - API: `RestExceptionHandler` returns structured JSON errors.

## External interfaces
- Web UI (Thymeleaf templates in `src/main/resources/templates/`).
- REST API under `/api/attendance`:
  - `POST /upload` (multipart CSV) -> returns attendance sheet payload.
  - `GET /sheet` -> returns current session sheet.
  - `POST /save` (JSON attendance submission) -> writes CSV, returns file info.
  - `GET /download/{fileName}` -> downloads a saved CSV.

## Data formats
- Upload CSV header: `cmsId,name,fatherName`.
- Output CSV header: `cmsId,name,fatherName,attendance`.
- Attendance values are `Present` or `Absent`.

## Improvements required
- Persistence layer: replace session and file-only storage with a database for multi-user and historical records.
- Authentication/authorization: protect upload/save/download endpoints for real school usage.
- Validation hardening:
  - Prevent CSV injection in exported files (leading `=`, `+`, `-`, `@`).
  - Normalize and validate `cmsId` format and length.
  - Reject unexpected extra columns rather than skipping malformed rows silently.
- Concurrency and naming:
  - Avoid filename collisions by adding seconds or a random suffix.
  - Guard against concurrent writes to the same file in high-traffic scenarios.
- API evolution:
  - Add versioning (e.g., `/api/v1/attendance`).
  - Add pagination and filters for large class lists when backed by a DB.
- Observability and operational needs:
  - Add request logging/correlation IDs.
  - Add metrics for upload/save failures.
- Testing:
  - Expand unit and integration tests for CSV parsing, validation edge cases, and API flows.
- UX enhancements:
  - Bulk mark present/absent.
  - Visual summary counts and attendance stats.
  - Upload preview with row-level validation errors.

## Typical use cases
- Small schools or coaching centers needing lightweight attendance without a database.
- Demo or training project for CSV processing, Spring MVC, and basic REST API design.
- Internal classroom attendance during workshops or short courses.
- Rapid prototypes where local file storage is acceptable and quick setup is required.

