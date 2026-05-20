package com.example.attendance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Attendance attendance = new Attendance();
    private Csv csv = new Csv();
    private Session session = new Session();

    @Getter
    @Setter
    public static class Attendance {
        private String recordsDirectory = "attendance-records";
    }

    @Getter
    @Setter
    public static class Csv {
        private String expectedHeaders = "cmsId,name,fatherName";
        private String attendanceHeader = "attendance";
        private int maxColumns = 3;
    }

    @Getter
    @Setter
    public static class Session {
        private String studentsAttribute = "uploadedStudents";
        private String savedFileAttribute = "savedAttendanceFile";
    }
}
