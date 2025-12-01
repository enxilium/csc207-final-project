package usecases.dashboard;

/**
 * Output boundary for the course dashboard use case.
 * Defines the interface for preparing dashboard views.
 */
public interface CourseDashboardOutputBoundary {
  /**
   * Prepares the success view for the dashboard Use Case.
   *
   * @param outputData the output data
   */
  void prepareDashboardView(CourseDashboardOutputData outputData);

  /**
   * Prepares the failure view for the dashboard Use Case.
   *
   * @param errorMessage the explanation of the failure
   */
  void prepareFailView(String errorMessage);

  /**
   * Prepares the create view for the Course Use Case.
   */
  void prepareCreateCourseView();
}
