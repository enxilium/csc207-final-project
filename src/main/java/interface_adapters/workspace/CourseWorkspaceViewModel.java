package interface_adapters.workspace;

import interface_adapters.ViewModel;

/**
 * View model for the course workspace view.
 */
public class CourseWorkspaceViewModel extends ViewModel<CourseState> {
  /**
   * Constructs a new CourseWorkspaceViewModel.
   */
  public CourseWorkspaceViewModel() {
    super("workspace");
    setState(new CourseState());
  }
}
