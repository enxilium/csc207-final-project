package usecases.lecturenotes;

import entities.Course;

/**
 * This interface lets the lecture-notes use case find a Course
 * without knowing HOW courses are stored or managed.
 * Later, the Course Management use case will implement this
 * and return real Course objects.
 * For now, you can also write a dummy implementation that
 * just returns a hard-coded Course for local testing.
 */
public interface CourseLookupGateway {

    /**
     * Return the Course with the given ID, or null if it doesn't exist.
     */
    Course getCourseById(String courseId);
}