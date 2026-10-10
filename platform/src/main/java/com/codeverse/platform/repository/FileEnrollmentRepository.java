package com.codeverse.platform.repository;

import com.codeverse.platform.model.Enrollment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "codeverse.storage", havingValue = "file")
public class FileEnrollmentRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    public FileEnrollmentRepository(@Value("${data.enrollments.path}") String path) {
        this.file = new File(path);
    }

    public List<Enrollment> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            List<Enrollment> loaded = objectMapper.readValue(file, new TypeReference<List<Enrollment>>() {});
            return new ArrayList<>(loaded); // mutable
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Optional<Enrollment> findById(int id) {
        return findAll().stream().filter(e -> e.getId() == id).findFirst();
    }

    public Optional<Enrollment> findByUserIdAndCourseId(int userId, int courseId) {
        return findAll().stream()
                .filter(e -> e.getUserId() == userId && e.getCourseId() == courseId)
                .findFirst();
    }

    public Enrollment save(Enrollment enrollment) {
        List<Enrollment> enrollments = findAll();

        if (enrollment.getId() == 0) {
            int nextId = enrollments.stream().mapToInt(Enrollment::getId).max().orElse(0) + 1;
            enrollment.setId(nextId);
            enrollments.add(enrollment);
        } else {
            boolean updated = false;
            for (int i = 0; i < enrollments.size(); i++) {
                if (enrollments.get(i).getId() == enrollment.getId()) {
                    enrollments.set(i, enrollment);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                enrollments.add(enrollment);
            }
        }

        writeAll(enrollments);
        return enrollment;
    }

    private void writeAll(List<Enrollment> enrollments) {
        try {
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, enrollments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write enrollments to file: " + file.getAbsolutePath(), e);
        }
    }
}
