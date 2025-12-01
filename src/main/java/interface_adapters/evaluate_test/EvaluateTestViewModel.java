package interface_adapters.evaluate_test;

import interface_adapters.ViewModel;

/**
 * View model for the evaluate test view.
 */
public class EvaluateTestViewModel extends ViewModel<EvaluateTestState> {
  /**
   * Constructs a new EvaluateTestViewModel.
   */
  public EvaluateTestViewModel() {
    super("evaluate test");
    setState(new EvaluateTestState());
  }
}
