package interface_adapters.workspace;

import interface_adapters.ViewModel;

/**
 * View model for the course creation view.
 */
public class CourseCreateViewModel extends ViewModel<CourseState> {
  /**
   * Constructs a new CourseCreateViewModel.
   */
  public CourseCreateViewModel() {
    super("createCourse");
    setState(new CourseState());
  }
}
