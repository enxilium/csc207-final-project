package interface_adapters.evaluate_test;

import usecases.evaluate_test.EvaluateTestInputBoundary;
import usecases.evaluate_test.EvaluateTestInputData;

import java.io.IOException;
import java.util.List;

public class EvaluateTestController {
    private final EvaluateTestInputBoundary evaluateTestInteractor;

    public EvaluateTestController(EvaluateTestInputBoundary evaluateTestInteractor) {
        this.evaluateTestInteractor = evaluateTestInteractor;
    }

    public void execute(String courseID, List<String> userAnswers, List<String> questions,
                        List<String> answers) {
        Thread worker = new Thread(() -> {
            EvaluateTestInputData inputData =
                    new EvaluateTestInputData(courseID, userAnswers, questions, answers);
            evaluateTestInteractor.execute(inputData);
        }, "evaluate-test-worker");
        worker.setDaemon(true);
        worker.start();
    }
}
