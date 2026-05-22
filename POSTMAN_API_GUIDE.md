# Postman API Guide

This project exposes a REST API under `/api/attendance`. The API is session-backed, so the usual flow is:

1. Upload a CSV file.
2. Fetch the generated attendance sheet.
3. Submit attendance marks.
4. Download the generated CSV file.

## Base URL

If the app runs locally on the default port:

```text
http://localhost:8080
```

## Recommended Postman Environment

Create one environment variable:

| Variable  | Value                   |
| --------- | ----------------------- |
| `baseUrl` | `http://localhost:8080` |

Use `{{baseUrl}}` in all requests below.

## 1. Upload CSV

### Request

- Method: `POST`
- URL: `{{baseUrl}}/api/attendance/upload`
- Body: `form-data`
- Key: `file` as type `File`

### Sample file

Use a CSV with this header:

```csv
cmsId,name,fatherName
101,Ali Khan,Ahmed Khan
102,Sara Ali,Rashid Ali
```

### Expected response

- Status: `201 Created`
- Content-Type: `application/json`

Example response:

```json
{
  "students": [
    {
      "cmsId": "101",
      "name": "Ali Khan",
      "fatherName": "Ahmed Khan",
      "attendanceStatus": "ABSENT"
    },
    {
      "cmsId": "102",
      "name": "Sara Ali",
      "fatherName": "Rashid Ali",
      "attendanceStatus": "ABSENT"
    }
  ],
  "savedFileName": null,
  "studentCount": 2
}
```

## 2. Get Attendance Sheet

### Request

- Method: `GET`
- URL: `{{baseUrl}}/api/attendance/sheet`

### Important

Call this after upload. If the session has expired or no students were uploaded, the API returns a validation error.

### Expected response

- Status: `200 OK`
- Content-Type: `application/json`

Example response:

```json
{
  "students": [
    {
      "cmsId": "101",
      "name": "Ali Khan",
      "fatherName": "Ahmed Khan",
      "attendanceStatus": "ABSENT"
    }
  ],
  "savedFileName": null,
  "studentCount": 1
}
```

## 3. Save Attendance

### Request

- Method: `POST`
- URL: `{{baseUrl}}/api/attendance/save`
- Body: `raw`
- Format: `JSON`

### Sample body

```json
{
  "students": [
    {
      "cmsId": "101",
      "attendanceStatus": "PRESENT"
    },
    {
      "cmsId": "102",
      "attendanceStatus": "ABSENT"
    }
  ]
}
```

### Expected response

- Status: `200 OK`
- Content-Type: `application/json`

Example response:

```json
{
  "fileName": "attendance_2026_05_22_11_57.csv",
  "filePath": "D:/Spring Boot/Student-Attendance-SpringBoot/attendance-records/attendance_2026_05_22_11_57.csv",
  "studentCount": 2
}
```

## 4. Download Attendance CSV

### Request

- Method: `GET`
- URL: `{{baseUrl}}/api/attendance/download/{fileName}`

Replace `{fileName}` with the file returned by the save endpoint.

Example:

```text
{{baseUrl}}/api/attendance/download/attendance_2026_05_22_11_57.csv
```

### Expected response

- Status: `200 OK`
- Content-Type: `text/csv`
- Header: `Content-Disposition: attachment; filename="<fileName>"`

This is a file response, so Postman will not show JSON here. A `200 OK` status is expected when the CSV stream is returned successfully.

### How to use in Postman

In Postman, the response will show as a file download or raw CSV. Use **Send and Download** if you want to save it directly.

If you see `200 OK` with no JSON body, that is still correct for this endpoint because the API is returning the CSV file itself.

## Suggested Test Order in Postman

1. `Upload CSV`
2. `Get Attendance Sheet`
3. `Save Attendance`
4. `Download Attendance CSV`

## Common Errors

### 400 Bad Request

Usually means one of these:

- No students were uploaded into the session.
- The uploaded CSV is invalid.
- The attendance payload is empty.
- The requested download file name is invalid.

### 404 Not Found

Usually means the download file does not exist in `attendance-records/`.

### 413 Payload Too Large

The uploaded file exceeds the configured maximum size.

## Notes

- The API relies on HTTP session state, so keep the same Postman session/cookies for the full flow.
- If you restart the app or lose the session, upload the CSV again before calling `/sheet`, `/save`, or `/download`.
