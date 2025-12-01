package interface_adapters.evaluate_test;

import java.io.IOException;
import java.util.List;
import usecases.evaluate_test.EvaluateTestInputBoundary;
import usecases.evaluate_test.EvaluateTestInputData;

/**
 * Controller for the evaluate test use case.
 */
public class EvaluateTestController {
  private final EvaluateTestInputBoundary evaluateTestInteractor;

  /**
   * Constructs an EvaluateTestController with the given input boundary.
   *
   * @param evaluateTestInteractor the interactor for evaluating tests
   */
  public EvaluateTestController(EvaluateTestInputBoundary evaluateTestInteractor) {
    this.evaluateTestInteractor = evaluateTestInteractor;
  }

  /**
   * Executes the test evaluation with the given parameters.
   *
   * @param courseId the course ID
   * @param userAnswers the user's answers
   * @param questions the questions
   * @param answers the correct answers
   */
  public void execute(String courseId, List<String> userAnswers, List<String> questions,
      List<String> answers) {
    Thread worker = new Thread(() -> {
      EvaluateTestInputData inputData =
          new EvaluateTestInputData(courseId, userAnswers, questions, answers);
      evaluateTestInteractor.execute(inputData);
    }, "evaluate-test-worker");
    worker.setDaemon(true);
    worker.start();
  }
}
