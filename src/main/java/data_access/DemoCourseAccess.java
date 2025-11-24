package data_access;

import entities.Course;
import entities.PDFFile;
import usecases.evaluate_test.EvaluateTestCourseDataAccessInterface;
import usecases.mock_test_generation.MockTestGenerationCourseDataAccessInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A concrete Data Access Object that simulates a database in memory.
 * It implements the specific interfaces required by different use cases, allowing them
 * to access course data without knowing the storage details.
 */
public class DemoCourseAccess implements
        MockTestGenerationCourseDataAccessInterface,
        EvaluateTestCourseDataAccessInterface {

    // The in-memory storage for all courses, acting as a database table.
    private final Map<String, Course> courses = new HashMap<>();

    /**
     * Saves a course to the in-memory storage. This method is used to
     * populate the DAO with initial or newly created data.
     * @param course The course entity to save.
     */
    public void save(Course course) {
        courses.put(course.getCourseId(), course);
    }

    // --- Implementation for EvaluateTestCourseDataAccessInterface ---
    @Override
    public List<PDFFile> getCourseMaterials(String courseId) {
        Course course = courses.get(courseId);
        if (course != null) {
            return course.getUploadedFiles();
        }
        // Return an empty list if the course is not found, preventing null pointer exceptions.
        return Collections.emptyList();
    }
}