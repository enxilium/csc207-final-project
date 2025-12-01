package usecases.evaluate_test;

/**
 * The mock test writing use case.
 */
public interface EvaluateTestInputBoundary {
  /**
   * Executes the mock test generation use case so users can write it.
   *
   * @param inputData the input data for evaluation
   */
  void execute(EvaluateTestInputData inputData);
}
