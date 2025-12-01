package usecases.workspace;

import entities.Course;
import java.util.List;
import java.util.Optional;
import usecases.ICourseRepository;
import usecases.dashboard.CourseDashboardOutputBoundary;
import usecases.dashboard.CourseDashboardOutputData;

/**
 * Interactor for the course workspace use case.
 * Handles finding, creating, updating, and deleting courses.
 */
public class CourseWorkspaceInteractor implements CourseWorkspaceInputBoundary {
  private final ICourseRepository courseRepository;
  private final CourseWorkspaceOutputBoundary courseWorkspacePresenter;
  private final CourseDashboardOutputBoundary courseDashboardPresenter;

  /**
   * Constructs a CourseWorkspaceInteractor with the given repository and presenters.
   *
   * @param courseRepository the repository for accessing course data
   * @param courseWorkspacePresenter the presenter for preparing workspace views
   * @param courseDashboardPresenter the presenter for preparing dashboard views
   */
  public CourseWorkspaceInteractor(ICourseRepository courseRepository,
      CourseWorkspaceOutputBoundary courseWorkspacePresenter,
      CourseDashboardOutputBoundary courseDashboardPresenter) {
    this.courseRepository = courseRepository;
    this.courseWorkspacePresenter = courseWorkspacePresenter;
    this.courseDashboardPresenter = courseDashboardPresenter;
  }

  /**
   * Finds a course by its ID and prepares either the workspace view or edit view.
   *
   * @param courseId the ID of the course to find
   * @param isEdit if true, prepares the edit view; otherwise prepares the workspace view
   */
  @Override
  public void findCourseById(String courseId, boolean isEdit) {
    Course course = this.courseRepository.findById(courseId);
    if (course == null) {
      this.courseWorkspacePresenter.prepareFailView("There is no course");
    } else {
      CourseWorkspaceOutputData courseWorkspaceOutputData =
          new CourseWorkspaceOutputData(course);
      if (isEdit) {
        this.courseWorkspacePresenter.prepareEditView(courseWorkspaceOutputData);
      } else {
        this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
      }
    }
  }

  /**
   * Creates a new course in the repository and prepares the workspace view.
   *
   * @param course the course to create
   * @throws IllegalArgumentException if the course is null or the course ID is null
   * @throws RuntimeException if a course with the same ID already exists
   */
  @Override
  public void createCourse(Course course) {
    if (course == null) {
      throw new IllegalArgumentException("course is null");
    }
    if (course.getCourseId() == null) {
      throw new IllegalArgumentException("course id is null");
    }

    List<Course> courses = this.courseRepository.findAll();
    Optional<Course> foundObject = courses.stream()
        .filter(obj -> obj.getCourseId().equals(course.getCourseId()))
        .findFirst();
    if (foundObject.isPresent()) {
      throw new RuntimeException("course already exist, course id: " + course.getCourseId());
    }
    this.courseRepository.create(course);
    CourseWorkspaceOutputData courseWorkspaceOutputData =
        new CourseWorkspaceOutputData(course);
    this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
  }

  /**
   * Updates an existing course in the repository and prepares the workspace view.
   *
   * @param course the course to update
   * @throws IllegalArgumentException if the course is null or the course ID is null
   * @throws RuntimeException if the course does not exist
   */
  @Override
  public void updateCourse(Course course) {
    if (course == null) {
      throw new IllegalArgumentException("course is null");
    }
    if (course.getCourseId() == null) {
      throw new IllegalArgumentException("course id is null");
    }

    List<Course> courses = this.courseRepository.findAll();
    Optional<Course> foundObject = courses.stream()
        .filter(obj -> obj.getCourseId().equals(course.getCourseId()))
        .findFirst();
    if (!foundObject.isPresent()) {
      throw new RuntimeException("course does not exist, course id: " + course.getCourseId());
    }
    this.courseRepository.update(course);
    CourseWorkspaceOutputData courseWorkspaceOutputData =
        new CourseWorkspaceOutputData(course);
    this.courseWorkspacePresenter.prepareWorkspaceView(courseWorkspaceOutputData);
  }

  /**
   * Deletes a course from the repository and updates the dashboard view.
   *
   * @param courseId the ID of the course to delete
   * @throws IllegalArgumentException if the course ID is null
   */
  @Override
  public void deleteCourse(String courseId) {
    if (courseId == null) {
      throw new IllegalArgumentException("course id is null");
    }

    this.courseRepository.delete(courseId);
    List<Course> courses = this.courseRepository.findAll();

    CourseDashboardOutputData courseDashboardOutputData =
        new CourseDashboardOutputData(courses);
    this.courseDashboardPresenter.prepareDashboardView(courseDashboardOutputData);
  }
}
