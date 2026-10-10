package com.codeverse.platform.repository;

import com.codeverse.platform.model.Review;
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
public class FileReviewRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    public FileReviewRepository(@Value("${data.reviews.path}") String path) {
        this.file = new File(path);
    }

    public List<Review> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            List<Review> loaded = objectMapper.readValue(file, new TypeReference<List<Review>>() {});
            return new ArrayList<>(loaded); // mutable
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Optional<Review> findById(int id) {
        return findAll().stream().filter(r -> r.getId() == id).findFirst();
    }

    public Optional<Review> findByUserIdAndCourseId(int userId, int courseId) {
        return findAll().stream()
                .filter(r -> r.getUserId() == userId && r.getCourseId() == courseId)
                .findFirst();
    }

    public Review save(Review review) {
        List<Review> reviews = findAll();

        if (review.getId() == 0) {
            int nextId = reviews.stream().mapToInt(Review::getId).max().orElse(0) + 1;
            review.setId(nextId);
            reviews.add(review);
        } else {
            boolean updated = false;
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getId() == review.getId()) {
                    reviews.set(i, review);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                reviews.add(review);
            }
        }

        writeAll(reviews);
        return review;
    }

    private void writeAll(List<Review> reviews) {
        try {
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, reviews);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write reviews to file: " + file.getAbsolutePath(), e);
        }
    }
}
