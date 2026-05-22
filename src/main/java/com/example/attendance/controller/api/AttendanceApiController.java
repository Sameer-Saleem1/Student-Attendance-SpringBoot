    package com.example.attendance.controller.api;

    import com.example.attendance.config.AppConstants;
    import com.example.attendance.dto.AttendanceSaveResultDto;
    import com.example.attendance.dto.AttendanceSheetResponseDto;
    import com.example.attendance.dto.AttendanceSubmissionDto;
    import com.example.attendance.dto.StudentDto;
    import com.example.attendance.model.Student;
    import com.example.attendance.service.AttendanceService;
    import com.example.attendance.service.SessionService;
    import com.example.attendance.service.StudentService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.core.io.Resource;
    import org.springframework.core.io.UrlResource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.multipart.MultipartFile;

    import java.net.MalformedURLException;
    import java.nio.file.Path;
    import java.util.List;

    @RestController
    @RequestMapping("/api/attendance")
    @RequiredArgsConstructor
    public class AttendanceApiController {

        private final StudentService studentService;
        private final AttendanceService attendanceService;
        private final SessionService sessionService;

        @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<AttendanceSheetResponseDto> uploadCsv(@RequestParam("file") MultipartFile file) {
            List<Student> students = studentService.processUploadedCsv(file);
            sessionService.storeStudents(students);
            sessionService.clearSavedFile();

            return ResponseEntity.status(201).body(buildSheetResponse(students, null));
        }

        @GetMapping("/sheet")
        public ResponseEntity<AttendanceSheetResponseDto> getAttendanceSheet() {
            sessionService.requireStudentsInSession();
            List<Student> students = sessionService.getStoredStudents();

            return ResponseEntity.ok(buildSheetResponse(students, sessionService.getSavedFileName()));
        }

        @PostMapping("/save")
        public ResponseEntity<AttendanceSaveResultDto> saveAttendance(@RequestBody AttendanceSubmissionDto submission) {
            sessionService.requireStudentsInSession();
            List<Student> updatedStudents = attendanceService.applyAttendanceMarks(
                    sessionService.getStoredStudents(), submission);
            sessionService.storeStudents(updatedStudents);

            AttendanceSaveResultDto result = attendanceService.saveAttendance(updatedStudents);
            sessionService.storeSavedFileName(result.getFileName());

            return ResponseEntity.ok(result);
        }

        @GetMapping("/download/{fileName}")
        public ResponseEntity<Resource> downloadAttendance(@PathVariable String fileName)
                throws MalformedURLException {
            Path filePath = attendanceService.resolveAttendanceFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(AppConstants.CONTENT_TYPE_CSV))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        }

        private AttendanceSheetResponseDto buildSheetResponse(List<Student> students, String savedFileName) {
            return AttendanceSheetResponseDto.builder()
                    .students(students.stream().map(StudentDto::fromEntity).toList())
                    .studentCount(students.size())
                    .savedFileName(savedFileName)
                    .build();
        }
    }