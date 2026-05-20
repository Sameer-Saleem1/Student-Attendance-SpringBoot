package com.example.attendance.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileStorageConfig {

    private final AppProperties appProperties;

    @PostConstruct
    public void initStorageDirectory() throws IOException {
        Path recordsPath = Path.of(appProperties.getAttendance().getRecordsDirectory());
        if (!Files.exists(recordsPath)) {
            Files.createDirectories(recordsPath);
            log.info("Created attendance records directory at: {}", recordsPath.toAbsolutePath());
        }
    }
}
