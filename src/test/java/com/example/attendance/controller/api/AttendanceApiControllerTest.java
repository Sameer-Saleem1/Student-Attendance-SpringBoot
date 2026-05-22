package com.example.attendance.controller.api;

import com.example.attendance.dto.AttendanceSaveResultDto;
import com.example.attendance.dto.AttendanceSubmissionDto;
import com.example.attendance.exception.RestExceptionHandler;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.model.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.SessionService;
import com.example.attendance.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceApiController.class)
@Import(RestExceptionHandler.class)
class AttendanceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private SessionService sessionService;

    @Test
    void uploadCsvReturnsStudentsAndStoresSessionState() throws Exception {
        List<Student> students = List.of(
                Student.builder().cmsId("1").name("Alice").fatherName("Bob").build()
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                "cmsId,name,fatherName\n1,Alice,Bob".getBytes()
        );

        when(studentService.processUploadedCsv(any())).thenReturn(students);

        mockMvc.perform(multipart("/api/attendance/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentCount").value(1))
                .andExpect(jsonPath("$.students[0].cmsId").value("1"))
                .andExpect(jsonPath("$.students[0].attendanceStatus").value("ABSENT"));

        verify(sessionService).storeStudents(students);
        verify(sessionService).clearSavedFile();
    }

    @Test
    void saveAttendanceAppliesMarksAndReturnsSaveResult() throws Exception {
        List<Student> sessionStudents = List.of(
                Student.builder().cmsId("1").name("Alice").fatherName("Bob").build()
        );
        AttendanceSubmissionDto submission = new AttendanceSubmissionDto(
                List.of(new AttendanceSubmissionDto.StudentAttendanceEntry("1", AttendanceStatus.PRESENT))
        );

        List<Student> updatedStudents = List.of(
                Student.builder().cmsId("1").name("Alice").fatherName("Bob")
                        .attendanceStatus(AttendanceStatus.PRESENT)
                        .build()
        );

        when(sessionService.getStoredStudents()).thenReturn(sessionStudents);
        doNothing().when(sessionService).requireStudentsInSession();
        when(attendanceService.applyAttendanceMarks(sessionStudents, submission)).thenReturn(updatedStudents);
        when(attendanceService.saveAttendance(updatedStudents)).thenReturn(AttendanceSaveResultDto.builder()
                .fileName("attendance.csv")
                .filePath(Path.of("attendance-records", "attendance.csv").toString())
                .studentCount(1)
                .build());

        mockMvc.perform(post("/api/attendance/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"students\":[{\"cmsId\":\"1\",\"attendanceStatus\":\"PRESENT\"}]}") )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("attendance.csv"))
                .andExpect(jsonPath("$.studentCount").value(1));

        verify(sessionService).storeStudents(updatedStudents);
        verify(sessionService).storeSavedFileName("attendance.csv");
    }

    @Test
    void uploadCsvReturnsValidationErrorAsJson() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                "cmsId,name,fatherName\n1,Alice,Bob".getBytes()
        );

        when(studentService.processUploadedCsv(any())).thenThrow(new com.example.attendance.exception.ValidationException(
                "Uploaded file is not valid."
        ));

        mockMvc.perform(multipart("/api/attendance/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Uploaded file is not valid."))
                .andExpect(jsonPath("$.path").value("/api/attendance/upload"));
    }

        @Test
        void downloadAttendanceStreamsCsvFile() throws Exception {
                Path tempFile = Files.createTempFile("attendance-test", ".csv");
                Files.writeString(tempFile, "cmsId,name,fatherName,attendance\n1,Alice,Bob,Present\n");

                when(attendanceService.resolveAttendanceFile("attendance.csv")).thenReturn(tempFile);

                mockMvc.perform(get("/api/attendance/download/attendance.csv"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Content-Disposition", "attachment; filename=\"attendance.csv\""))
                                .andExpect(content().contentType("text/csv"))
                                .andExpect(content().string("cmsId,name,fatherName,attendance\n1,Alice,Bob,Present\n"));
        }
}