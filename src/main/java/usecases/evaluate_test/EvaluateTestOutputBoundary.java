package usecases.evaluate_test;

/**
 * Output boundary for the evaluate test use case.
 */
public interface EvaluateTestOutputBoundary {
  /**
   * Presents the evaluation results.
   *
   * @param evaluateTestOutputData the output data
   */
  void presentEvaluationResults(EvaluateTestOutputData evaluateTestOutputData);

  /**
   * Presents a loading state.
   */
  void presentLoading();

  /**
   * Presents an error message.
   *
   * @param errorMessage the error message
   */
  void presentError(String errorMessage);
}
