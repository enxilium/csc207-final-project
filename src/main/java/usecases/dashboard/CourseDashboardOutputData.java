package usecases.dashboard;

import entities.Course;
import java.util.List;

/**
 * Output data model for the course dashboard use case.
 * Contains the list of courses to be displayed on the dashboard.
 */
public class CourseDashboardOutputData {
  private List<Course> courses;

  /**
   * Constructs a CourseDashboardOutputData with the given list of courses.
   *
   * @param courses the list of courses to display
   */
  public CourseDashboardOutputData(List<Course> courses) {
    this.courses = courses;
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
   * Returns the number of courses in the output data.
   *
   * @return the number of courses, or 0 if the courses list is null
   */
  public int size() {
    if (this.courses == null) {
      return 0;
    }
    return this.courses.size();
  }
}
