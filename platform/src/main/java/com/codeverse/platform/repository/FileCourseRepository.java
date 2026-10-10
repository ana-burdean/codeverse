package com.codeverse.platform.repository;

import com.codeverse.platform.model.Course;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "codeverse.storage", havingValue = "file")
public class FileCourseRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    public FileCourseRepository(@Value("${data.courses.path}") String path) {
        this.file = new File(path);
    }

    public List<Course> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            List<Course> loaded = objectMapper.readValue(file, new TypeReference<List<Course>>() {});
            return new ArrayList<>(loaded); // mutable
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read courses from file: " + file.getAbsolutePath(), e);
        }
    }

    public Optional<Course> findById(int id) {
        return findAll().stream().filter(c -> c.getId() == id).findFirst();
    }

    public Course save(Course course) {
        List<Course> courses = findAll();

        if (course.getId() == 0) {
            int nextId = courses.stream().mapToInt(Course::getId).max().orElse(0) + 1;
            course.setId(nextId);
            courses.add(course);
        } else {
            boolean updated = false;
            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getId() == course.getId()) {
                    courses.set(i, course);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                courses.add(course);
            }
        }

        writeAll(courses);
        return course;
    }

    public void deleteById(int id) {
        List<Course> courses = findAll();
        courses.removeIf(c -> c.getId() == id);
        writeAll(courses);
    }

    private void writeAll(List<Course> courses) {
        try {
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, courses);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write courses to file: " + file.getAbsolutePath(), e);
        }
    }
}
