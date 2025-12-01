package interface_adapters;

import interface_adapters.mock_test.MockTestState;

/**
 * ViewModel for the loading view.
 */
public class LoadingViewModel extends ViewModel<MockTestState> {
  /**
   * Constructs a LoadingViewModel.
   */
  public LoadingViewModel() {
    super("loading");
    setState(new MockTestState());
  }
}
