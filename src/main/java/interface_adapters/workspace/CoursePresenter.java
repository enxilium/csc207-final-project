package interface_adapters.workspace;

import interface_adapters.ViewManagerModel;
import interface_adapters.dashboard.CourseDashboardState;
import interface_adapters.dashboard.CourseDashboardViewModel;
import usecases.workspace.CourseWorkspaceOutputBoundary;
import usecases.workspace.CourseWorkspaceOutputData;

/**
 * Presenter for the course workspace use case.
 * Prepares view models and manages view transitions for the workspace.
 */
public class CoursePresenter implements CourseWorkspaceOutputBoundary {
  private final CourseDashboardViewModel courseDashboardViewModel;
  private final CourseWorkspaceViewModel courseWorkspaceViewModel;
  private final ViewManagerModel viewManagerModel;
  private final CourseEditViewModel courseEditViewModel;

  /**
   * Constructs a CoursePresenter with the given view models.
   *
   * @param viewManagerModel the model for managing view transitions
   * @param courseDashboardViewModel the view model for the dashboard
   * @param courseWorkspaceViewModel the view model for the workspace
   * @param courseEditViewModel the view model for course editing
   */
  public CoursePresenter(ViewManagerModel viewManagerModel,
      CourseDashboardViewModel courseDashboardViewModel,
      CourseWorkspaceViewModel courseWorkspaceViewModel,
      CourseEditViewModel courseEditViewModel) {
    this.viewManagerModel = viewManagerModel;
    this.courseDashboardViewModel = courseDashboardViewModel;
    this.courseWorkspaceViewModel = courseWorkspaceViewModel;
    this.courseEditViewModel = courseEditViewModel;
  }

  @Override
  public void prepareWorkspaceView(CourseWorkspaceOutputData response) {
    final CourseState courseWorkspaceState = courseWorkspaceViewModel.getState();

    courseWorkspaceState.setCourse(response.getCourse());
    this.courseWorkspaceViewModel.firePropertyChange();

    // and clear everything from the dashboard's state
    this.courseDashboardViewModel.setState(new CourseDashboardState());

    // switch to the workspace in view
    this.viewManagerModel.setState(courseWorkspaceViewModel.getViewName());
    this.viewManagerModel.firePropertyChange();
  }

  @Override
  public void prepareEditView(CourseWorkspaceOutputData response) {
    final CourseState courseWorkspaceState = courseEditViewModel.getState();

    courseWorkspaceState.setCourse(response.getCourse());
    this.courseEditViewModel.firePropertyChange();

    // switch to the workspace in view
    this.viewManagerModel.setState(courseEditViewModel.getViewName());
    this.viewManagerModel.firePropertyChange();
  }

  @Override
  public void prepareFailView(String errorMessage) {
    // Empty implementation - error handling can be added here if needed
  }
}
