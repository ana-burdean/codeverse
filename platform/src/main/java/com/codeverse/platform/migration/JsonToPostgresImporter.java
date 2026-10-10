package com.codeverse.platform.migration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JsonToPostgresImporter {

    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/codeverse";
    private static final String DB_USER = "codeverse";
    private static final String DB_PASS = "codeverse";

    private static final String DATA_DIR = "data";

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CourseJson {
        public int id;
        public String title;
        public String description;
        public Integer studentCount;

        @JsonProperty("lessons")
        public List<LessonJson> lessons;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LessonJson {
        public int id;
        public int courseId;
        public String title;
        public String content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserJson {
        public int id;
        public String username;
        public String password;
        public String role;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnrollmentJson {
        public int id;
        public int userId;
        public int courseId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReviewJson {
        public int id;
        public int userId;
        public int courseId;
        public int rating;
        public String comment;
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        File base = new File(DATA_DIR);
        System.out.println("Looking for JSON files in: " + base.getAbsolutePath());

        if (!base.exists() || !base.isDirectory()) {
            throw new IllegalStateException("Missing data folder: " + base.getAbsolutePath());
        }

        File usersFile       = new File(base, "users.json");
        File coursesFile     = new File(base, "courses.json");
        File enrollmentsFile = new File(base, "enrollments.json");
        File reviewsFile     = new File(base, "reviews.json");

        List<UserJson> users = loadIfExists(mapper, usersFile, new TypeReference<List<UserJson>>() {});
        List<CourseJson> courses = loadIfExists(mapper, coursesFile, new TypeReference<List<CourseJson>>() {});
        List<EnrollmentJson> enrollments = loadIfExists(mapper, enrollmentsFile, new TypeReference<List<EnrollmentJson>>() {});
        List<ReviewJson> reviews = loadIfExists(mapper, reviewsFile, new TypeReference<List<ReviewJson>>() {});

        List<LessonJson> lessons = new ArrayList<>();
        for (CourseJson c : courses) {
            if (c.lessons != null) {
                for (LessonJson l : c.lessons) {
                    if (l.courseId == 0) l.courseId = c.id;
                    lessons.add(l);
                }
            }
        }

        System.out.println("Found:");
        System.out.println(" - users.json: " + (usersFile.exists() ? users.size() + " rows" : "MISSING (skipped)"));
        System.out.println(" - courses.json: " + (coursesFile.exists() ? courses.size() + " rows" : "MISSING (skipped)"));
        System.out.println(" - embedded lessons in courses.json: " + lessons.size() + " rows");
        System.out.println(" - enrollments.json: " + (enrollmentsFile.exists() ? enrollments.size() + " rows" : "MISSING (skipped)"));
        System.out.println(" - reviews.json: " + (reviewsFile.exists() ? reviews.size() + " rows" : "MISSING (skipped)"));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);
            System.out.println("Connected to DB: " + DB_URL);

            // order
            if (!users.isEmpty()) upsertUsersByUsername(conn, users);
            if (!courses.isEmpty()) upsertCourses(conn, courses);
            if (!lessons.isEmpty()) upsertLessons(conn, lessons);
            if (!enrollments.isEmpty()) upsertEnrollments(conn, enrollments);
            if (!reviews.isEmpty()) upsertReviews(conn, reviews);

            // sync sequences (users id sequence still should be >= max(id))
            syncIdentity(conn, "users", "id");
            syncIdentity(conn, "courses", "id");
            syncIdentity(conn, "lessons", "id");
            syncIdentity(conn, "enrollments", "id");
            syncIdentity(conn, "reviews", "id");

            conn.commit();
            System.out.println("✅ Import complete!");
        }
    }

    private static <T> List<T> loadIfExists(ObjectMapper mapper, File file, TypeReference<List<T>> type) throws Exception {
        if (!file.exists()) return List.of();
        return mapper.readValue(file, type);
    }


    private static void upsertUsersByUsername(Connection conn, List<UserJson> users) throws SQLException {
        System.out.println("Importing users (upsert by username)...");
        String sql = """
            INSERT INTO users (username, password, role)
            VALUES (?, ?, ?)
            ON CONFLICT (username) DO UPDATE SET
              password = EXCLUDED.password,
              role = EXCLUDED.role
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (UserJson u : users) {
                if (u.username == null || u.username.isBlank()) continue;

                ps.setString(1, u.username);

                // Keep existing hash from JSON (looks like BCrypt already)
                ps.setString(2, u.password);

                String role = (u.role == null || u.role.isBlank())
                        ? "STUDENT"
                        : u.role.trim().toUpperCase();
                ps.setString(3, role);

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void upsertCourses(Connection conn, List<CourseJson> courses) throws SQLException {
        System.out.println("Importing courses...");
        String sql = """
            INSERT INTO courses (id, title, description)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              title = EXCLUDED.title,
              description = EXCLUDED.description
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CourseJson c : courses) {
                ps.setInt(1, c.id);
                ps.setString(2, c.title);
                ps.setString(3, c.description);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void upsertLessons(Connection conn, List<LessonJson> lessons) throws SQLException {
        System.out.println("Importing lessons (embedded in courses.json)...");
        String sql = """
            INSERT INTO lessons (id, course_id, title, content)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              course_id = EXCLUDED.course_id,
              title = EXCLUDED.title,
              content = EXCLUDED.content
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LessonJson l : lessons) {
                ps.setInt(1, l.id);
                ps.setInt(2, l.courseId);
                ps.setString(3, l.title);
                ps.setString(4, l.content);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void upsertEnrollments(Connection conn, List<EnrollmentJson> enrollments) throws SQLException {
        System.out.println("Importing enrollments...");
        String sql = """
            INSERT INTO enrollments (id, user_id, course_id)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              user_id = EXCLUDED.user_id,
              course_id = EXCLUDED.course_id
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (EnrollmentJson e : enrollments) {
                ps.setInt(1, e.id);
                ps.setInt(2, e.userId);
                ps.setInt(3, e.courseId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void upsertReviews(Connection conn, List<ReviewJson> reviews) throws SQLException {
        System.out.println("Importing reviews...");
        String sql = """
            INSERT INTO reviews (id, user_id, course_id, rating, comment)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              user_id = EXCLUDED.user_id,
              course_id = EXCLUDED.course_id,
              rating = EXCLUDED.rating,
              comment = EXCLUDED.comment
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ReviewJson r : reviews) {
                ps.setInt(1, r.id);
                ps.setInt(2, r.userId);
                ps.setInt(3, r.courseId);
                ps.setInt(4, r.rating);
                ps.setString(5, r.comment);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void syncIdentity(Connection conn, String table, String idCol) throws SQLException {
        String seqSql = "SELECT pg_get_serial_sequence(?, ?)";
        String maxSql = "SELECT COALESCE(MAX(" + idCol + "), 0) FROM " + table;

        String seqName;
        try (PreparedStatement ps = conn.prepareStatement(seqSql)) {
            ps.setString(1, table);
            ps.setString(2, idCol);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                seqName = rs.getString(1);
            }
        }

        if (seqName == null) {
            System.out.println("No sequence for " + table + "." + idCol + " (skipping sync)");
            return;
        }

        long maxId;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(maxSql)) {
            rs.next();
            maxId = rs.getLong(1);
        }

        try (Statement st = conn.createStatement()) {
            st.execute("SELECT setval('" + seqName + "', " + Math.max(maxId, 1) + ")");
        }

        System.out.println("Synced identity for " + table + " to >= " + maxId);
    }
}
