package usecases.dashboard;

import entities.Course;
import java.util.List;
import usecases.ICourseRepository;

/**
 * Interactor for the course dashboard use case.
 * Handles retrieving all courses and navigating to the course creation view.
 */
public class CourseDashboardInteractor implements CourseDashboardInputBoundary {
  private final ICourseRepository courseRepository;
  private final CourseDashboardOutputBoundary coursePresenter;

  /**
   * Constructs a CourseDashboardInteractor with the given repository and presenter.
   *
   * @param courseRepository the repository for accessing course data
   * @param coursePresenter the presenter for preparing dashboard views
   */
  public CourseDashboardInteractor(ICourseRepository courseRepository,
      CourseDashboardOutputBoundary coursePresenter) {
    this.courseRepository = courseRepository;
    this.coursePresenter = coursePresenter;
  }

  /**
   * Retrieves all courses from the repository and prepares the dashboard view.
   * If no courses are found, prepares a failure view with an error message.
   */
  @Override
  public void getCourses() {
    List<Course> courses = this.courseRepository.findAll();
    if (courses == null || courses.isEmpty()) {
      this.coursePresenter.prepareFailView("There is no course");
    } else {
      CourseDashboardOutputData courseDashboardOutputData =
          new CourseDashboardOutputData(courses);
      this.coursePresenter.prepareDashboardView(courseDashboardOutputData);
    }
  }

  /**
   * Prepares the view for creating a new course.
   */
  @Override
  public void createCourse() {
    this.coursePresenter.prepareCreateCourseView();
  }
}
