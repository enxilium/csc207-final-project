package usecases;

import entities.Course;
import java.util.List;

/**
 * Repository interface for course data access operations.
 */
public interface ICourseRepository {
  /**
   * Creates a new course.
   *
   * @param course the course to create
   */
  void create(Course course);

  /**
   * Updates an existing course.
   *
   * @param course the course to update
   */
  void update(Course course);

  /**
   * Finds a course by its ID.
   *
   * @param courseId the course ID
   * @return the course, or null if not found
   */
  Course findById(String courseId);

  /**
   * Finds all courses.
   *
   * @return a list of all courses
   */
  List<Course> findAll();

  /**
   * Deletes a course by its ID.
   *
   * @param courseId the course ID
   */
  void delete(String courseId);
}
