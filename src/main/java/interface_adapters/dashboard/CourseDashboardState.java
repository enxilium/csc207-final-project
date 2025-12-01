package interface_adapters.dashboard;

import entities.Course;
import java.util.List;

/**
 * State class for the course dashboard view model.
 */
public class CourseDashboardState {
  private List<Course> courses;
  private String error;

  /**
   * Constructs a new CourseDashboardState.
   */
  public CourseDashboardState() {
  }

  /**
   * Gets the list of courses.
   *
   * @return the list of courses
   */
  public List<Course> getCourses() {
    return this.courses;
  }

  /**
   * Sets the list of courses.
   *
   * @param courses the list of courses to set
   */
  public void setCourses(List<Course> courses) {
    this.courses = courses;
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
