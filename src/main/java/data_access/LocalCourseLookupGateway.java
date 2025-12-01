package data_access;

import entities.Course;
import usecases.lecturenotes.CourseLookupGateway;

/**
 * Local implementation of CourseLookupGateway using LocalCourseRepository.
 */
public class LocalCourseLookupGateway implements CourseLookupGateway {
  private final LocalCourseRepository repo;

  /**
   * Constructs a LocalCourseLookupGateway with the given repository.
   *
   * @param repo the course repository to use
   */
  public LocalCourseLookupGateway(LocalCourseRepository repo) {
    this.repo = repo;
  }

  @Override
  public Course getCourseById(String courseId) {
    return repo.findById(courseId);
  }
}
