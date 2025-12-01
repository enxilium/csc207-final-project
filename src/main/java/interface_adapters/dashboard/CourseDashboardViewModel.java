package interface_adapters.dashboard;

import interface_adapters.ViewModel;

/**
 * View model for the course dashboard view.
 */
public class CourseDashboardViewModel extends ViewModel<CourseDashboardState> {
  /**
   * Constructs a new CourseDashboardViewModel.
   */
  public CourseDashboardViewModel() {
    super("dashboard");
    setState(new CourseDashboardState());
  }
}
