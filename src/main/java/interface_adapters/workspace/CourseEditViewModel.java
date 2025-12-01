package interface_adapters.workspace;

import interface_adapters.ViewModel;

/**
 * View model for the course edit view.
 */
public class CourseEditViewModel extends ViewModel<CourseState> {
  /**
   * Constructs a new CourseEditViewModel.
   */
  public CourseEditViewModel() {
    super("editCourse");
    setState(new CourseState());
  }
}
