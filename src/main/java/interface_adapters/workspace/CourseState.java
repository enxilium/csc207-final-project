package interface_adapters.workspace;

import entities.Course;

/**
 * State class for course workspace and edit view models.
 */
public class CourseState {
  private Course course;
  private String error;

  /**
   * Constructs a new CourseState.
   */
  public CourseState() {
  }

  /**
   * Gets the course.
   *
   * @return the course
   */
  public Course getCourse() {
    return this.course;
  }

  /**
   * Sets the course.
   *
   * @param course the course to set
   */
  public void setCourse(Course course) {
    this.course = course;
  }

  /**
   * Sets the error message.
   *
   * @param error the error message to set
   */
  public void setError(String error) {
    this.error = error;
  }
}
